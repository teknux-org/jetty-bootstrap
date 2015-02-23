/*******************************************************************************
 * (C) Copyright 2014 Teknux.org (http://teknux.org/).
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 * Contributors:
 *      "Pierre PINON"
 *      "Francois EYL"
 *      "Laurent MARCHAL"
 *  
 *******************************************************************************/
package org.teknux.jettybootstrap.keystore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;


public class JettyKeystoreGeneratorBuilder extends AbstractJettyKeystore {

    public static final String SIGNATURE_ALGORITHM_SHA256WITHRSA = "SHA256WithRSAEncryption";

    public static final String DEFAULT_ALGORITHM = ALGORITHM_RSA;
    public static final String DEFAULT_SIGNATURE_ALGORITHM = SIGNATURE_ALGORITHM_SHA256WITHRSA;
    public static final int DEFAULT_DATE_NOT_BEFORE_NUMBER_OF_DAYS = 30;
    public static final int DEFAULT_DATE_NOT_AFTER_NUMBER_OF_DAYS = 3650;

    private static final Long DAY_IN_MILLIS = 1000L * 60 * 60 * 24;

    private String algorithm = DEFAULT_ALGORITHM;
    private String signatureAlgorithm = DEFAULT_SIGNATURE_ALGORITHM;
    private String rdnOuValue;
    private String rdnOValue;
    private int dateNotBeforeNumberOfDays = DEFAULT_DATE_NOT_BEFORE_NUMBER_OF_DAYS;
    private int dateNotAfterNumberOfDays = DEFAULT_DATE_NOT_AFTER_NUMBER_OF_DAYS;

    public String getAlgorithm() {
        return algorithm;
    }

    public JettyKeystoreGeneratorBuilder setAlgorithm(String algorithm) {
        this.algorithm = algorithm;

        return this;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public JettyKeystoreGeneratorBuilder setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;

        return this;
    }

    public String getRdnOuValue() {
        return rdnOuValue;
    }

    public JettyKeystoreGeneratorBuilder setRdnOuValue(String rdnOuValue) {
        this.rdnOuValue = rdnOuValue;

        return this;
    }

    public String getRdnOValue() {
        return rdnOValue;
    }

    public JettyKeystoreGeneratorBuilder setRdnOValue(String rdnOValue) {
        this.rdnOValue = rdnOValue;

        return this;
    }

    public int getDateNotBeforeNumberOfDays() {
        return dateNotBeforeNumberOfDays;
    }

    public JettyKeystoreGeneratorBuilder setDateNotBeforeNumberOfDays(int dateNotBeforeNumberOfDays) {
        this.dateNotBeforeNumberOfDays = dateNotBeforeNumberOfDays;

        return this;
    }

    public int getDateNotAfterNumberOfDays() {
        return dateNotAfterNumberOfDays;
    }

    public JettyKeystoreGeneratorBuilder setDateNotAfterNumberOfDays(int dateNotAfterNumberOfDays) {
        this.dateNotAfterNumberOfDays = dateNotAfterNumberOfDays;

        return this;
    }

    public KeyStore build(String domainName, String alias, String password) throws JettyKeystoreException {
        return build(domainName, alias, password, true);
    }

    public KeyStore build(String domainName, String alias, String password, boolean checkValidity) throws JettyKeystoreException {
        Objects.requireNonNull(domainName, "DomainName is required");
        Objects.requireNonNull(alias, "Alias is required");
        Objects.requireNonNull(password, "Password is required");

        KeyPair keyPair = generateKeyPair(algorithm);
        Certificate certificate = generateCertificate(keyPair, domainName, signatureAlgorithm, rdnOuValue, rdnOValue, dateNotBeforeNumberOfDays, dateNotAfterNumberOfDays);

        KeyStore keystore = createKeyStore(keyPair.getPrivate(), certificate, alias, password);

        if (checkValidity) {
            checkValidity(keystore, alias);
        }

        return keystore;
    }

    private static KeyPair generateKeyPair(String algorithm) throws JettyKeystoreException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);

            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_CREATE_KEYS, "Can not generate private and public keys", e);
        }
    }

    private static Certificate generateCertificate(KeyPair keyPair, String domainName, String signatureAlgorithm, String rdnOuValue, String rdnOValue,
            int dateNotBeforeNumberOfDays, int dateNotAfterNumberOfDays) throws JettyKeystoreException {

        X500NameBuilder issuerX500Namebuilder = new X500NameBuilder(BCStyle.INSTANCE);
        if (rdnOuValue != null) {
            issuerX500Namebuilder.addRDN(BCStyle.OU, rdnOuValue);
        }
        if (rdnOValue != null) {
            issuerX500Namebuilder.addRDN(BCStyle.O, rdnOValue);
        }
        X500Name issuer = issuerX500Namebuilder.addRDN(BCStyle.CN, domainName).build();

        BigInteger serial = BigInteger.valueOf(Math.abs(new SecureRandom().nextInt()));
        Date dateNotBefore = new Date(System.currentTimeMillis() - (dateNotBeforeNumberOfDays * DAY_IN_MILLIS));
        Date dateNotAfter = new Date(System.currentTimeMillis() + (dateNotAfterNumberOfDays * DAY_IN_MILLIS));

        X500NameBuilder subjectX500Namebuilder = new X500NameBuilder(BCStyle.INSTANCE);
        if (rdnOuValue != null) {
            subjectX500Namebuilder.addRDN(BCStyle.OU, rdnOuValue);
        }
        if (rdnOValue != null) {
            subjectX500Namebuilder.addRDN(BCStyle.O, rdnOValue);
        }
        X500Name subject = subjectX500Namebuilder.addRDN(BCStyle.CN, domainName).build();

        SubjectPublicKeyInfo publicKeyInfo = new SubjectPublicKeyInfo(ASN1Sequence.getInstance(keyPair.getPublic().getEncoded()));

        X509v3CertificateBuilder x509v3CertificateBuilder = new X509v3CertificateBuilder(issuer, serial, dateNotBefore, dateNotAfter, subject, publicKeyInfo);

        Provider provider = new BouncyCastleProvider();

        try {
            ContentSigner signer = new JcaContentSignerBuilder(signatureAlgorithm).setProvider(provider).build(keyPair.getPrivate());

            return new JcaX509CertificateConverter().setProvider(provider).getCertificate(x509v3CertificateBuilder.build(signer));
        } catch (OperatorCreationException | CertificateException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_CREATE_CERTIFICATE, "Can not generate certificate", e);
        }
    }

    public static void saveKeyStore(KeyStore keystore, File file, String password) throws JettyKeystoreException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {

            keystore.store(fileOutputStream, password.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_SAVE_KEYSTORE, "Can not save keystore file", e);
        }
    }

    public void checkValidity() throws JettyKeystoreException {
        build("testDomain", "testAlias", "testPassword", true);
    }
}

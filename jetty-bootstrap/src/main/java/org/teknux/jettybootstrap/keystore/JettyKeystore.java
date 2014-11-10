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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
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
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.DecoderException;


public class JettyKeystore {

    public static final String ALGORITHM_RSA = "RSA";
    public static final String SIGNATURE_ALGORITHM_SHA256WITHRSA = "SHA256WithRSAEncryption";

    private static final String CERTIFICATE_TYPE_X509 = "X.509";

    public static final String DEFAULT_ALGORITHM = ALGORITHM_RSA;
    public static final String DEFAULT_SIGNATURE_ALGORITHM = SIGNATURE_ALGORITHM_SHA256WITHRSA;
    public static final int DEFAULT_DATE_NOT_BEFORE_NUMBER_OF_DAYS = 30;
    public static final int DEFAULT_DATE_NOT_AFTER_NUMBER_OF_DAYS = 3650;

    private static final String PRIVATE_KEY_HEADER_FOOTER_PATTERN = "(^|\n)-----.*-----($|\n)";
    private static final Long DAY_IN_MILLIS = 1000L * 60 * 60 * 24;

    private String algorithm = DEFAULT_ALGORITHM;
    private String signatureAlgorithm = DEFAULT_SIGNATURE_ALGORITHM;
    private String alias;
    private String password;
    private String rdnOuValue;
    private String rdnOValue;
    private int dateNotBeforeNumberOfDays = DEFAULT_DATE_NOT_BEFORE_NUMBER_OF_DAYS;
    private int dateNotAfterNumberOfDays = DEFAULT_DATE_NOT_AFTER_NUMBER_OF_DAYS;

    public JettyKeystore(String alias, String password) {
        this.alias = Objects.requireNonNull(alias, "Alias is required");
        this.password = Objects.requireNonNull(password, "Password is required");
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = Objects.requireNonNull(alias, "Alias is required");
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Objects.requireNonNull(password, "Password is required");
    }

    public String getRdnOuValue() {
        return rdnOuValue;
    }

    public void setRdnOuValue(String rdnOuValue) {
        this.rdnOuValue = rdnOuValue;
    }

    public String getRdnOValue() {
        return rdnOValue;
    }

    public void setRdnOValue(String rdnOValue) {
        this.rdnOValue = rdnOValue;
    }

    public int getDateNotBeforeNumberOfDays() {
        return dateNotBeforeNumberOfDays;
    }

    public void setDateNotBeforeNumberOfDays(int dateNotBeforeNumberOfDays) {
        this.dateNotBeforeNumberOfDays = dateNotBeforeNumberOfDays;
    }

    public int getDateNotAfterNumberOfDays() {
        return dateNotAfterNumberOfDays;
    }

    public void setDateNotAfterNumberOfDays(int dateNotAfterNumberOfDays) {
        this.dateNotAfterNumberOfDays = dateNotAfterNumberOfDays;
    }

    public KeyStore generateKeyStore(String domainName) throws JettyKeystoreException {
        Objects.requireNonNull(domainName, "DomainName is required");

        KeyPair keyPair = generateKeyPair(algorithm);
        Certificate certificate = generateCertificate(keyPair, domainName, signatureAlgorithm, rdnOuValue, rdnOValue, dateNotBeforeNumberOfDays, dateNotAfterNumberOfDays);

        KeyStore keyStore = createKeyStore(keyPair.getPrivate(), certificate, alias, password);

        return keyStore;
    }

    public void generateKeyStoreAndSave(String domainName, File file) throws JettyKeystoreException {
        Objects.requireNonNull(domainName, "DomainName is required");
        Objects.requireNonNull(file, "File is required");

        KeyStore keyStore = generateKeyStore(domainName);
        saveKeyStore(keyStore, file, password);
    }

    public KeyStore convertToKeyStore(File privateKeyFile, File certificateFile) throws JettyKeystoreException {
        if (!Objects.requireNonNull(privateKeyFile, "PrivateKeyFile is required").canRead()) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_READ_PRIVATE_KEY, "Can not read private key file");
        }
        if (!Objects.requireNonNull(certificateFile, "CertificateFile is required").canRead()) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_READ_CERTIFICATE, "Can not read certificate file");
        }

        FileInputStream privateKeyOutputStream = null;
        FileInputStream certificateOutputStream = null;
        try {
            try {
                privateKeyOutputStream = new FileInputStream(privateKeyFile);

            } catch (FileNotFoundException e) {
                throw new JettyKeystoreException(JettyKeystoreException.ERROR_READ_PRIVATE_KEY, "Can not read private key file", e);
            }

            try {
                certificateOutputStream = new FileInputStream(certificateFile);
            } catch (FileNotFoundException e) {
                throw new JettyKeystoreException(JettyKeystoreException.ERROR_READ_CERTIFICATE, "Can not read certificate file", e);
            }

            return convertToKeyStore(privateKeyOutputStream, certificateOutputStream);
        } finally {
            if (privateKeyOutputStream != null) {
                try {
                    privateKeyOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (certificateOutputStream != null) {
                try {
                    certificateOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public KeyStore convertToKeyStore(InputStream privateKeyInputStream, InputStream certificateInputStream) throws JettyKeystoreException {
        Objects.requireNonNull(privateKeyInputStream, "PrivateKeyFile is required");
        Objects.requireNonNull(certificateInputStream, "CertificateFile is required");

        PrivateKey privateKey = loadPrivateKey(privateKeyInputStream, algorithm);
        Certificate certificate = loadCertificate(certificateInputStream);

        KeyStore keyStore = createKeyStore(privateKey, certificate, alias, password);

        return keyStore;
    }

    public void convertToKeyStoreAndSave(File privateKeyFile, File certificateFile, File file) throws JettyKeystoreException {
        Objects.requireNonNull(privateKeyFile, "PrivateKeyFile is required");
        Objects.requireNonNull(certificateFile, "CertificateFile is required");
        Objects.requireNonNull(file, "File is required");

        KeyStore keyStore = convertToKeyStore(privateKeyFile, certificateFile);
        saveKeyStore(keyStore, file, password);
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

    private static KeyPair generateKeyPair(String algorithm) throws JettyKeystoreException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_CREATE_KEYS, "Can not generate private and public keys", e);
        }
    }

    private static Certificate loadCertificate(InputStream certificateOutputStream) throws JettyKeystoreException {

        try {
            CertificateFactory certificateFactory;
            certificateFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE_X509);
            Certificate certificate = certificateFactory.generateCertificate(certificateOutputStream);

            return certificate;
        } catch (CertificateException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_LOAD_CERTIFICATE, "Can not load certificate", e);
        }
    }

    private static PrivateKey loadPrivateKey(InputStream privateKeyOutputStream, String algorithm) throws JettyKeystoreException {
        try {
            String contentKeyFile = IOUtils.toString(privateKeyOutputStream);
            String contentKeyFileWithoutHeaderAndFooter = contentKeyFile.replaceAll(PRIVATE_KEY_HEADER_FOOTER_PATTERN, "");
            byte[] decodedKeyFile = Base64.decode(contentKeyFileWithoutHeaderAndFooter);

            KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decodedKeyFile);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            return privateKey;
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | DecoderException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_LOAD_PRIVATE_KEY, "Can not load private key", e);
        }
    }

    private static KeyStore createKeyStore(PrivateKey privateKey, Certificate certificate, String alias, String password) throws JettyKeystoreException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);

            PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry(privateKey, new Certificate[] { certificate });
            keyStore.setEntry(alias, privateKeyEntry, new KeyStore.PasswordProtection(password.toCharArray()));

            return keyStore;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_CREATE_KEYSTORE, "Can not create keystore file", e);
        }
    }

    private static void saveKeyStore(KeyStore keyStore, File file, String password) throws JettyKeystoreException {
        FileOutputStream fileInputStream = null;

        try {
            fileInputStream = new FileOutputStream(file);
            keyStore.store(fileInputStream, password.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_SAVE_KEYSTORE, "Can not save keystore file", e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

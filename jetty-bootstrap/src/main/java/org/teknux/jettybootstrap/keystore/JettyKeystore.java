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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

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


public class JettyKeystore {

	public static final String ALGORITHM_RSA = "RSA";
	public static final String SIGNATURE_ALGORITHM_SHA256WITHRSA = "SHA256WithRSAEncryption";

	private static final String DEFAULT_ALGORITHM = ALGORITHM_RSA;
	private static final String DEFAULT_SIGNATURE_ALGORITHM = SIGNATURE_ALGORITHM_SHA256WITHRSA;

	private String RDN_OU_VALUE = "None";
	private String RDN_O_VALUE = "None";
	private Long DAY_IN_MILLIS = 1000L * 60 * 60 * 24;

	private String domainName;
	private String alias;
	private String password;

	public static void generateKeystoreAndSave(String domainName, String alias, String password, File file) throws JettyKeystoreException {
		JettyKeystore jettyKeystore = new JettyKeystore(domainName, alias, password);
		jettyKeystore.generateAndSave(file);
	}

	public static void generateKeystoreAndSave(String domainName, String alias, String password, File file, String algorithm, String signatureAlgorithm)
			throws JettyKeystoreException {
		JettyKeystore jettyKeystore = new JettyKeystore(domainName, alias, password);
		jettyKeystore.generateAndSave(file, algorithm, signatureAlgorithm);
	}

	public JettyKeystore(String domainName, String alias, String password) {
		this.domainName = domainName;
		this.alias = alias;
		this.password = password;
	}

	public void generateAndSave(File file) throws JettyKeystoreException {
		KeyStore keyStore = generate();
		saveKeyStore(keyStore, file, password);
	}

	public void generateAndSave(File file, String algorithm, String signatureAlgorithm) throws JettyKeystoreException {
		KeyStore keyStore = generate(algorithm, signatureAlgorithm);
		saveKeyStore(keyStore, file, password);
	}

	public KeyStore generate() throws JettyKeystoreException {
		return generate(DEFAULT_ALGORITHM, DEFAULT_SIGNATURE_ALGORITHM);
	}

	public KeyStore generate(String algorithm, String signatureAlgorithm) throws JettyKeystoreException {
		KeyPair keyPair = generateKeyPair(algorithm);
		X509Certificate x509Certificate = generateX509Certificate(keyPair, domainName, signatureAlgorithm);

		return generateKeyStore(keyPair, x509Certificate, alias, password);
	}

	private KeyPair generateKeyPair(String algorithm) throws JettyKeystoreException {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(algorithm);
			return keyPairGenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			throw new JettyKeystoreException(e);
		}
	}

	private X509Certificate generateX509Certificate(KeyPair keyPair, String domainName, String signatureAlgorithm) throws JettyKeystoreException {
		X500Name issuer = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.OU, RDN_OU_VALUE).addRDN(BCStyle.O, RDN_O_VALUE).addRDN(BCStyle.CN, domainName).build();
		BigInteger serial = BigInteger.valueOf(Math.abs(new SecureRandom().nextInt()));
		Date dateNotBefore = new Date(System.currentTimeMillis() - (30 * DAY_IN_MILLIS));
		Date dateNotAfter = new Date(System.currentTimeMillis() + (3650 * DAY_IN_MILLIS));
		X500Name subject = new X500NameBuilder(BCStyle.INSTANCE).addRDN(BCStyle.OU, RDN_OU_VALUE).addRDN(BCStyle.O, RDN_O_VALUE).addRDN(BCStyle.CN, domainName).build();
		SubjectPublicKeyInfo publicKeyInfo = new SubjectPublicKeyInfo(ASN1Sequence.getInstance(keyPair.getPublic().getEncoded()));

		X509v3CertificateBuilder x509v3CertificateBuilder = new X509v3CertificateBuilder(issuer, serial, dateNotBefore, dateNotAfter, subject, publicKeyInfo);

		Provider provider = new BouncyCastleProvider();

		try {
			ContentSigner signer = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM_SHA256WITHRSA).setProvider(provider).build(keyPair.getPrivate());

			return new JcaX509CertificateConverter().setProvider(provider).getCertificate(x509v3CertificateBuilder.build(signer));
		} catch (OperatorCreationException e) {
			throw new JettyKeystoreException(e);
		} catch (CertificateException e) {
			throw new JettyKeystoreException(e);
		}
	}

	private KeyStore generateKeyStore(KeyPair keyPair, X509Certificate x509Certificate, String alias, String password) throws JettyKeystoreException {
		try {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(null, null);

			PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry(keyPair.getPrivate(), new Certificate[] { x509Certificate });
			keyStore.setEntry(alias, privateKeyEntry, new KeyStore.PasswordProtection(password.toCharArray()));

			return keyStore;
		} catch (KeyStoreException e) {
			throw new JettyKeystoreException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new JettyKeystoreException(e);
		} catch (CertificateException e) {
			throw new JettyKeystoreException(e);
		} catch (IOException e) {
			throw new JettyKeystoreException(e);
		}
	}

	private void saveKeyStore(KeyStore keyStore, File file, String password) throws JettyKeystoreException {
		FileOutputStream fileInputStream = null;
		try {
			fileInputStream = new FileOutputStream(file);
			keyStore.store(fileInputStream, password.toCharArray());
		} catch (FileNotFoundException e) {
			throw new JettyKeystoreException(e);
		} catch (KeyStoreException e) {
			throw new JettyKeystoreException(e);
		} catch (NoSuchAlgorithmException e) {
			throw new JettyKeystoreException(e);
		} catch (CertificateException e) {
			throw new JettyKeystoreException(e);
		} catch (IOException e) {
			throw new JettyKeystoreException(e);
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

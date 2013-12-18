package org.genux.jettybootstrap.keystore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;


public class JettyKeystore {

	public static final String ALGORITHM_RSA = "RSA";
	public static final String SIGNATURE_ALGORITHM_MD5WITHRSAENCRYPTION = "MD5WithRSAEncryption";
	public static final String SIGNATURE_ALGORITHM_SHA256WITHRSA = "SHA256withRSA";

	private static final String DEFAULT_ALGORITHM = ALGORITHM_RSA;
	private static final String DEFAULT_SIGNATURE_ALGORITHM = SIGNATURE_ALGORITHM_SHA256WITHRSA;

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
		Security.addProvider(new BouncyCastleProvider());

		X509V3CertificateGenerator x509V3CertificateGenerator = new X509V3CertificateGenerator();
		x509V3CertificateGenerator.setSerialNumber(BigInteger.valueOf(Math.abs(new SecureRandom().nextInt())));
		x509V3CertificateGenerator.setIssuerDN(new X509Principal("CN=" + domainName + ", OU=None, O=None L=None, C=None"));
		x509V3CertificateGenerator.setNotBefore(new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30));
		x509V3CertificateGenerator.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * 10)));
		x509V3CertificateGenerator.setSubjectDN(new X509Principal("CN=" + domainName + ", OU=None, O=None L=None, C=None"));

		x509V3CertificateGenerator.setPublicKey(keyPair.getPublic());
		x509V3CertificateGenerator.setSignatureAlgorithm(signatureAlgorithm);

		try {
			return x509V3CertificateGenerator.generateX509Certificate(keyPair.getPrivate());
		} catch (InvalidKeyException e) {
			throw new JettyKeystoreException(e);
		} catch (SecurityException e) {
			throw new JettyKeystoreException(e);
		} catch (SignatureException e) {
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

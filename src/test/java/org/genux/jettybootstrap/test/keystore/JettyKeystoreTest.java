package org.genux.jettybootstrap.test.keystore;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;

import org.bouncycastle.jce.provider.X509CertificateObject;
import org.genux.jettybootstrap.keystore.JettyKeystore;
import org.genux.jettybootstrap.keystore.JettyKeystoreException;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JettyKeystoreTest {

	private static final String KEYSTORE_DOMAINNAME = "unknown";
	private static final String KEYSTORE_ALIAS = "jettybootstraptest";
	private static final String KEYSTORE_PASSWORD = "jettybootstraptest";

	@Test
	public void do01KeystoreTest() throws CertificateExpiredException, CertificateNotYetValidException, JettyKeystoreException, KeyStoreException {
		JettyKeystore jettyKeystore = new JettyKeystore(KEYSTORE_DOMAINNAME, KEYSTORE_ALIAS, KEYSTORE_PASSWORD);

		Assert.assertNotEquals(null, jettyKeystore);

		KeyStore keyStore = jettyKeystore.generate();

		Assert.assertNotEquals(null, keyStore);

		Certificate certificate = keyStore.getCertificate(KEYSTORE_ALIAS);

		Assert.assertNotEquals(null, certificate);
		Assert.assertEquals("X509CertificateObject", certificate.getClass().getSimpleName());
		X509CertificateObject x509CertificateObject = (X509CertificateObject) certificate;

		x509CertificateObject.checkValidity();
	}
}

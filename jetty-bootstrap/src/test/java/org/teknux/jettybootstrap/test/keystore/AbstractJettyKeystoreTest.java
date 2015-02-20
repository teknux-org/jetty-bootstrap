package org.teknux.jettybootstrap.test.keystore;

import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.junit.Assert;


public class AbstractJettyKeystoreTest {

    protected static final String KEYSTORE_ALIAS = "jettybootstraptest";
    protected static final String KEYSTORE_PASSWORD = "jettybootstraptest";

    protected static void checkValidity(KeyStore keystore, String keystoreAlias) throws KeyStoreException, InvalidKeyException, CertificateException, NoSuchAlgorithmException,
            NoSuchProviderException, SignatureException {
        Assert.assertNotNull(keystore);

        Certificate certificate = keystore.getCertificate(keystoreAlias);

        Assert.assertNotNull(certificate);
        X509Certificate x509Certificate = (X509Certificate) certificate;

        x509Certificate.checkValidity();
        x509Certificate.verify(certificate.getPublicKey());
    }
}

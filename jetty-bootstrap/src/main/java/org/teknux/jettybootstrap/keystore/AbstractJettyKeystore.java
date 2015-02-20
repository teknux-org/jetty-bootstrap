package org.teknux.jettybootstrap.keystore;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;


public class AbstractJettyKeystore {

    public static final String ALGORITHM_RSA = "RSA";

    protected static KeyStore createKeyStore(PrivateKey privateKey, Certificate certificate, String alias, String password) throws JettyKeystoreException {
        try {
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null, null);

            PrivateKeyEntry privateKeyEntry = new PrivateKeyEntry(privateKey, new Certificate[] { certificate });
            keystore.setEntry(alias, privateKeyEntry, new KeyStore.PasswordProtection(password.toCharArray()));

            return keystore;
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_CREATE_KEYSTORE, "Can not create keystore file", e);
        }
    }
}

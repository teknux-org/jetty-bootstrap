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

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Objects;


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

    public static void checkValidity(KeyStore keystore, String keystoreAlias, boolean checkValidity, boolean verifySignature) throws JettyKeystoreException {
        try {
            Objects.requireNonNull(keystore, "Keystore can not be null");
            Certificate certificate = keystore.getCertificate(keystoreAlias);

            Objects.requireNonNull(certificate, "Certificate is unreacheable");
            X509Certificate x509Certificate = (X509Certificate) certificate;

            if (checkValidity) {
                x509Certificate.checkValidity();
            }
            if (verifySignature) {
                x509Certificate.verify(certificate.getPublicKey());
            }
        } catch (NullPointerException | InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException | KeyStoreException e) {
            throw new JettyKeystoreException(JettyKeystoreException.ERROR_INVALID_KEYSTORE, "Keystore is not valid", e);
        }
    }
}

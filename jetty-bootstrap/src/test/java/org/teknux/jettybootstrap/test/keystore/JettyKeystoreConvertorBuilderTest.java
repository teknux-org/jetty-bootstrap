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
package org.teknux.jettybootstrap.test.keystore;

import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.teknux.jettybootstrap.keystore.JettyKeystoreConvertorBuilder;
import org.teknux.jettybootstrap.keystore.JettyKeystoreException;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JettyKeystoreConvertorBuilderTest {

    protected static final String KEYSTORE_ALIAS = "jettybootstraptest";
    protected static final String KEYSTORE_PASSWORD = "jettybootstraptest";

    @Test
    public void do01KeystoreTest() throws JettyKeystoreException, InvalidKeyException, KeyStoreException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException,
            SignatureException {
        InputStream inputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/jks/test.jks");

        JettyKeystoreConvertorBuilder jettyKeystoreConvertorBuilder = new JettyKeystoreConvertorBuilder().setKeystore(inputStream, "test");
        jettyKeystoreConvertorBuilder.checkValidity(true, true);

        KeyStore keystore = jettyKeystoreConvertorBuilder.build(KEYSTORE_ALIAS, KEYSTORE_PASSWORD);
        JettyKeystoreConvertorBuilder.checkValidity(keystore, KEYSTORE_ALIAS, true, true);
    }

    @Test
    public void do02KeyAndKeystoreTest() throws JettyKeystoreException, InvalidKeyException, KeyStoreException, CertificateException, NoSuchAlgorithmException,
            NoSuchProviderException, SignatureException {
        InputStream keyInputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/jks/test.key");
        InputStream crtInputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/jks/test.jks");

        JettyKeystoreConvertorBuilder jettyKeystoreConvertorBuilder = new JettyKeystoreConvertorBuilder().setPrivateKeyFromPKCS8(keyInputStream).setCertificateFromKeystore(
                crtInputStream, "test");
        jettyKeystoreConvertorBuilder.checkValidity(true, true);

        KeyStore keystore = jettyKeystoreConvertorBuilder.build(KEYSTORE_ALIAS, KEYSTORE_PASSWORD);
        JettyKeystoreConvertorBuilder.checkValidity(keystore, KEYSTORE_ALIAS, true, true);
    }

    @Test
    public void do03KeystoreAndCrtTest() throws JettyKeystoreException, InvalidKeyException, KeyStoreException, CertificateException, NoSuchAlgorithmException,
            NoSuchProviderException, SignatureException {
        InputStream keyInputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/jks/test.jks");
        InputStream crtInputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/jks/test.crt");

        JettyKeystoreConvertorBuilder jettyKeystoreConvertorBuilder = new JettyKeystoreConvertorBuilder().setPrivateKeyFromKeystore(keyInputStream, "test")
                .setCertificateFromPKCS8(crtInputStream);
        jettyKeystoreConvertorBuilder.checkValidity(true, true);

        KeyStore keystore = jettyKeystoreConvertorBuilder.build(KEYSTORE_ALIAS, KEYSTORE_PASSWORD);
        JettyKeystoreConvertorBuilder.checkValidity(keystore, KEYSTORE_ALIAS, true, true);
    }

    @Test
    public void do04PemTest() throws JettyKeystoreException, InvalidKeyException, KeyStoreException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException,
            SignatureException {
        InputStream inputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/pkcs8/test.pem");

        JettyKeystoreConvertorBuilder jettyKeystoreConvertorBuilder = new JettyKeystoreConvertorBuilder().setPKCS8(inputStream);
        jettyKeystoreConvertorBuilder.checkValidity(true, true);

        KeyStore keystore = jettyKeystoreConvertorBuilder.build(KEYSTORE_ALIAS, "test");
        JettyKeystoreConvertorBuilder.checkValidity(keystore, KEYSTORE_ALIAS, true, true);
    }

    @Test
    public void do05KeyAndCrtTest() throws JettyKeystoreException, InvalidKeyException, KeyStoreException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException,
            SignatureException {
        InputStream keyInputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/pkcs8/test.key");
        InputStream crtInputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/pkcs8/test.crt");

        JettyKeystoreConvertorBuilder jettyKeystoreConvertorBuilder = new JettyKeystoreConvertorBuilder().setPrivateKeyFromPKCS8(keyInputStream).setCertificateFromPKCS8(
                crtInputStream);
        jettyKeystoreConvertorBuilder.checkValidity(true, true);

        KeyStore keystore = jettyKeystoreConvertorBuilder.build(KEYSTORE_ALIAS, KEYSTORE_PASSWORD);
        JettyKeystoreConvertorBuilder.checkValidity(keystore, KEYSTORE_ALIAS, true, true);
    }

    @Test
    public void do06KeyAndPemTest() throws JettyKeystoreException, InvalidKeyException, KeyStoreException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException,
            SignatureException {
        InputStream keyInputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/pkcs8/test.key");
        InputStream crtInputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/pkcs8/test.pem");

        JettyKeystoreConvertorBuilder jettyKeystoreConvertorBuilder = new JettyKeystoreConvertorBuilder().setPrivateKeyFromPKCS8(keyInputStream).setCertificateFromPKCS8(
                crtInputStream);
        jettyKeystoreConvertorBuilder.checkValidity(true, true);

        KeyStore keystore = jettyKeystoreConvertorBuilder.build(KEYSTORE_ALIAS, KEYSTORE_PASSWORD);
        JettyKeystoreConvertorBuilder.checkValidity(keystore, KEYSTORE_ALIAS, true, true);
    }

    @Test
    public void do07PemAndCrtTest() throws JettyKeystoreException, InvalidKeyException, KeyStoreException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException,
            SignatureException {
        InputStream keyInputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/pkcs8/test.pem");
        InputStream crtInputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/pkcs8/test.crt");

        JettyKeystoreConvertorBuilder jettyKeystoreConvertorBuilder = new JettyKeystoreConvertorBuilder().setPrivateKeyFromPKCS8(keyInputStream).setCertificateFromPKCS8(
                crtInputStream);
        jettyKeystoreConvertorBuilder.checkValidity(true, true);

        KeyStore keystore = jettyKeystoreConvertorBuilder.build(KEYSTORE_ALIAS, KEYSTORE_PASSWORD);
        JettyKeystoreConvertorBuilder.checkValidity(keystore, KEYSTORE_ALIAS, true, true);
    }

    @Test
    public void do08P12Test() throws JettyKeystoreException, InvalidKeyException, KeyStoreException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException,
            SignatureException {
        InputStream inputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/pkcs12/test.p12");

        JettyKeystoreConvertorBuilder jettyKeystoreConvertorBuilder = new JettyKeystoreConvertorBuilder().setPKCS12(inputStream, "test");
        jettyKeystoreConvertorBuilder.checkValidity(true, true);

        KeyStore keystore = jettyKeystoreConvertorBuilder.build(KEYSTORE_ALIAS, KEYSTORE_PASSWORD);
        JettyKeystoreConvertorBuilder.checkValidity(keystore, KEYSTORE_ALIAS, true, true);
    }

    @Test
    public void do09KeyAndP12Test() throws JettyKeystoreException, InvalidKeyException, KeyStoreException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException,
            SignatureException {
        InputStream keyInputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/pkcs12/test.key");
        InputStream crtInputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/pkcs12/test.p12");

        JettyKeystoreConvertorBuilder jettyKeystoreConvertorBuilder = new JettyKeystoreConvertorBuilder().setPrivateKeyFromPKCS8(keyInputStream).setCertificateFromPKCS12(
                crtInputStream, "test");
        jettyKeystoreConvertorBuilder.checkValidity(true, true);

        KeyStore keystore = jettyKeystoreConvertorBuilder.build(KEYSTORE_ALIAS, KEYSTORE_PASSWORD);
        JettyKeystoreConvertorBuilder.checkValidity(keystore, KEYSTORE_ALIAS, true, true);
    }

    @Test
    public void do10P12AndCrtTest() throws JettyKeystoreException, InvalidKeyException, KeyStoreException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException,
            SignatureException {
        InputStream keyInputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/pkcs12/test.p12");
        InputStream crtInputStream = getClass().getResourceAsStream("/org/teknux/jettybootstrap/test/keystore/pkcs12/test.crt");

        JettyKeystoreConvertorBuilder jettyKeystoreConvertorBuilder = new JettyKeystoreConvertorBuilder().setPrivateKeyFromPKCS12(keyInputStream, "test").setCertificateFromPKCS8(
                crtInputStream);
        jettyKeystoreConvertorBuilder.checkValidity(true, true);

        KeyStore keystore = jettyKeystoreConvertorBuilder.build(KEYSTORE_ALIAS, KEYSTORE_PASSWORD);
        JettyKeystoreConvertorBuilder.checkValidity(keystore, KEYSTORE_ALIAS, true, true);
    }
}

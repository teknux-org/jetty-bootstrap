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

import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.teknux.jettybootstrap.keystore.JettyKeystoreException;
import org.teknux.jettybootstrap.keystore.JettyKeystoreGeneratorBuilder;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JettyKeystoreGeneratorBuilderTest extends AbstractJettyKeystoreTest {

    private static final String KEYSTORE_DOMAINNAME = "unknowntest";

    @Test
    public void do01GenerateKeystoreTest() throws JettyKeystoreException, KeyStoreException, InvalidKeyException, CertificateException, NoSuchAlgorithmException,
            NoSuchProviderException, SignatureException {
        JettyKeystoreGeneratorBuilder jettyKeystore = new JettyKeystoreGeneratorBuilder();

        Assert.assertNotEquals(null, jettyKeystore);

        KeyStore keystore = jettyKeystore.build(KEYSTORE_DOMAINNAME, KEYSTORE_ALIAS, KEYSTORE_PASSWORD);

        checkValidity(keystore, KEYSTORE_ALIAS);
    }
}

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

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;

import org.bouncycastle.jce.provider.X509CertificateObject;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.teknux.jettybootstrap.keystore.JettyKeystore;
import org.teknux.jettybootstrap.keystore.JettyKeystoreException;


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

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
package org.teknux.jettybootstrap.test.configuration;

import java.io.File;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.teknux.jettybootstrap.configuration.JettyConnector;
import org.teknux.jettybootstrap.configuration.PropertiesJettyConfiguration;
import org.teknux.jettybootstrap.keystore.JettyKeystore;


public class PropertiesJettyConfigurationTest {

    @Test
    public void parseJettyConnetorsTest() {
        Assert.assertArrayEquals(null, PropertiesJettyConfiguration.parseConnectors(new String[] { "ef", "SeSL" }));
        Assert.assertArrayEquals(new JettyConnector[] { JettyConnector.HTTPS }, PropertiesJettyConfiguration.parseConnectors(new String[] { "ef", "HTTPS" }));
        Assert.assertArrayEquals(new JettyConnector[] { JettyConnector.HTTP }, PropertiesJettyConfiguration.parseConnectors(new String[] { "HTTP" }));
        Assert.assertArrayEquals(new JettyConnector[] { JettyConnector.HTTP, JettyConnector.HTTPS }, PropertiesJettyConfiguration.parseConnectors(new String[] { "HTTP", "HTTPS" }));
    }

    @Test
    public void systemPropertiesTest() {
        //system properties
        System.setProperty(PropertiesJettyConfiguration.KEY_AUTO_JOIN_ON_START, "true");
        System.setProperty(PropertiesJettyConfiguration.KEY_MAX_THREADS, "1");
        System.setProperty(PropertiesJettyConfiguration.KEY_STOP_AT_SHUTDOWN, "false");
        System.setProperty(PropertiesJettyConfiguration.KEY_STOP_TIMEOUT, "2");
        System.setProperty(PropertiesJettyConfiguration.KEY_IDLE_TIMEOUT, "3");
        System.setProperty(PropertiesJettyConfiguration.KEY_HOST, "localhost1");
        System.setProperty(PropertiesJettyConfiguration.KEY_PORT, "4444");
        System.setProperty(PropertiesJettyConfiguration.KEY_SSL_PORT, "5555");
        System.setProperty(PropertiesJettyConfiguration.KEY_CONNECTORS, "HTTPS");
        System.setProperty(PropertiesJettyConfiguration.KEY_REDIRECT_WEBAPPS_ON_HTTPS, "false");
        System.setProperty(PropertiesJettyConfiguration.KEY_SSL_PRIVATEKEY_PATH, "./privatekey1.key");
        System.setProperty(PropertiesJettyConfiguration.KEY_SSL_CERTIFICATE_PATH, "./certificate1.crt");
        System.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_PATH, "./keystore1");
        System.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_PATH, "./keystore1");
        System.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_DOMAINNAME, "domain1");
        System.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_ALIAS, "alias1");
        System.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_PASSWORD, "pwd1");
        System.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_ALGORITHM, JettyKeystore.ALGORITHM_RSA);
        System.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_SIGNATURE_ALGORITHM, JettyKeystore.SIGNATURE_ALGORITHM_SHA256WITHRSA);
        System.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_RDN_OU_VALUE, "rdnou1");
        System.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_RDN_O_VALUE, "rdno1");
        System.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_DATE_NOT_BEFORE_NUMBER_OF_DAYS, "10");
        System.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_DATE_NOT_AFTER_NUMBER_OF_DAYS, "300");
        System.setProperty(PropertiesJettyConfiguration.KEY_TEMP_DIR, "/tmp1");
        System.setProperty(PropertiesJettyConfiguration.KEY_PERSIST_APP_TEMP_DIR, "false");
        System.setProperty(PropertiesJettyConfiguration.KEY_CLEAN_TEMP_DIR, "false");
        System.setProperty(PropertiesJettyConfiguration.KEY_PARENT_LOADER_PRIORITY, "true");

        //test sys prop config only
        PropertiesJettyConfiguration cfg = new PropertiesJettyConfiguration();
        Assert.assertEquals(true, cfg.isAutoJoinOnStart());
        Assert.assertEquals(1, cfg.getMaxThreads());
        Assert.assertEquals(false, cfg.isStopAtShutdown());
        Assert.assertEquals(2L, cfg.getStopTimeout());
        Assert.assertEquals(3L, cfg.getIdleTimeout());
        Assert.assertEquals("localhost1", cfg.getHost());
        Assert.assertEquals(4444, cfg.getPort());
        Assert.assertEquals(5555, cfg.getSslPort());
        Assert.assertTrue(cfg.getJettyConnectors().size() == 1);
        Assert.assertTrue(cfg.getJettyConnectors().contains(JettyConnector.HTTPS));
        Assert.assertEquals(false, cfg.isRedirectWebAppsOnHttpsConnector());
        Assert.assertEquals("./privatekey1.key", cfg.getSslPrivateKeyPath());
        Assert.assertEquals("./certificate1.crt", cfg.getSslCertificatePath());
        Assert.assertEquals("./keystore1", cfg.getSslKeyStorePath());
        Assert.assertEquals("domain1", cfg.getSslKeyStoreDomainName());
        Assert.assertEquals("alias1", cfg.getSslKeyStoreAlias());
        Assert.assertEquals("pwd1", cfg.getSslKeyStorePassword());
        Assert.assertEquals(JettyKeystore.ALGORITHM_RSA, cfg.getSslKeyStoreAlgorithm());
        Assert.assertEquals(JettyKeystore.SIGNATURE_ALGORITHM_SHA256WITHRSA, cfg.getSslKeyStoreSignatureAlgorithm());
        Assert.assertEquals("rdnou1", cfg.getSslKeyStoreRdnOuValue());
        Assert.assertEquals("rdno1", cfg.getSslKeyStoreRdnOValue());
        Assert.assertEquals(10, cfg.getSslKeyStoreDateNotBeforeNumberOfDays());
        Assert.assertEquals(300, cfg.getSslKeyStoreDateNotAfterNumberOfDays());
        Assert.assertEquals(new File("/tmp1"), cfg.getTempDirectory());
        Assert.assertEquals(false, cfg.isPersistAppTempDirectories());
        Assert.assertEquals(false, cfg.isCleanTempDir());
        Assert.assertEquals(true, cfg.isParentLoaderPriority());

        //custom properties
        final Properties properties = new Properties();
        properties.setProperty(PropertiesJettyConfiguration.KEY_AUTO_JOIN_ON_START, "false");
        properties.setProperty(PropertiesJettyConfiguration.KEY_MAX_THREADS, "11");
        properties.setProperty(PropertiesJettyConfiguration.KEY_STOP_AT_SHUTDOWN, "true");
        properties.setProperty(PropertiesJettyConfiguration.KEY_STOP_TIMEOUT, "22");
        properties.setProperty(PropertiesJettyConfiguration.KEY_IDLE_TIMEOUT, "33");
        properties.setProperty(PropertiesJettyConfiguration.KEY_HOST, "localhost11");
        properties.setProperty(PropertiesJettyConfiguration.KEY_PORT, "8080");
        properties.setProperty(PropertiesJettyConfiguration.KEY_SSL_PORT, "8443");
        properties.setProperty(PropertiesJettyConfiguration.KEY_CONNECTORS, "HTTP,HTTPS");
        properties.setProperty(PropertiesJettyConfiguration.KEY_REDIRECT_WEBAPPS_ON_HTTPS, "true");
        properties.setProperty(PropertiesJettyConfiguration.KEY_SSL_PRIVATEKEY_PATH, "./privatekey2.key");
        properties.setProperty(PropertiesJettyConfiguration.KEY_SSL_CERTIFICATE_PATH, "./certificate2.crt");
        properties.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_PATH, "./keystore2");
        properties.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_DOMAINNAME, "domain2");
        properties.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_ALIAS, "alias2");
        properties.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_PASSWORD, "pwd2");
        properties.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_ALGORITHM, JettyKeystore.ALGORITHM_RSA);
        properties.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_SIGNATURE_ALGORITHM, JettyKeystore.SIGNATURE_ALGORITHM_SHA256WITHRSA);
        properties.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_RDN_OU_VALUE, "rdnou2");
        properties.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_RDN_O_VALUE, "rdno2");
        properties.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_DATE_NOT_BEFORE_NUMBER_OF_DAYS, "15");
        properties.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_DATE_NOT_AFTER_NUMBER_OF_DAYS, "350");
        properties.setProperty(PropertiesJettyConfiguration.KEY_TEMP_DIR, "/tmp2");
        properties.setProperty(PropertiesJettyConfiguration.KEY_PERSIST_APP_TEMP_DIR, "true");
        properties.setProperty(PropertiesJettyConfiguration.KEY_CLEAN_TEMP_DIR, "true");
        properties.setProperty(PropertiesJettyConfiguration.KEY_PARENT_LOADER_PRIORITY, "false");

        //test given prop config only
        cfg = new PropertiesJettyConfiguration(properties, true);
        Assert.assertEquals(false, cfg.isAutoJoinOnStart());
        Assert.assertEquals(11, cfg.getMaxThreads());
        Assert.assertEquals(true, cfg.isStopAtShutdown());
        Assert.assertEquals(22L, cfg.getStopTimeout());
        Assert.assertEquals(33L, cfg.getIdleTimeout());
        Assert.assertEquals("localhost11", cfg.getHost());
        Assert.assertEquals(8080, cfg.getPort());
        Assert.assertEquals(8443, cfg.getSslPort());
        Assert.assertTrue(cfg.getJettyConnectors().size() == 2);
        Assert.assertTrue(cfg.getJettyConnectors().contains(JettyConnector.HTTPS));
        Assert.assertTrue(cfg.getJettyConnectors().contains(JettyConnector.HTTP));
        Assert.assertEquals(true, cfg.isRedirectWebAppsOnHttpsConnector());
        Assert.assertEquals("./privatekey2.key", cfg.getSslPrivateKeyPath());
        Assert.assertEquals("./certificate2.crt", cfg.getSslCertificatePath());
        Assert.assertEquals("./keystore2", cfg.getSslKeyStorePath());
        Assert.assertEquals("domain2", cfg.getSslKeyStoreDomainName());
        Assert.assertEquals("alias2", cfg.getSslKeyStoreAlias());
        Assert.assertEquals("pwd2", cfg.getSslKeyStorePassword());
        Assert.assertEquals(JettyKeystore.ALGORITHM_RSA, cfg.getSslKeyStoreAlgorithm());
        Assert.assertEquals(JettyKeystore.SIGNATURE_ALGORITHM_SHA256WITHRSA, cfg.getSslKeyStoreSignatureAlgorithm());
        Assert.assertEquals("rdnou2", cfg.getSslKeyStoreRdnOuValue());
        Assert.assertEquals("rdno2", cfg.getSslKeyStoreRdnOValue());
        Assert.assertEquals(15, cfg.getSslKeyStoreDateNotBeforeNumberOfDays());
        Assert.assertEquals(350, cfg.getSslKeyStoreDateNotAfterNumberOfDays());
        Assert.assertEquals(new File("/tmp2"), cfg.getTempDirectory());
        Assert.assertEquals(true, cfg.isPersistAppTempDirectories());
        Assert.assertEquals(true, cfg.isCleanTempDir());
        Assert.assertEquals(false, cfg.isParentLoaderPriority());

        //test sys prop and custom config with system having higher priority
        cfg = new PropertiesJettyConfiguration(properties);
        Assert.assertEquals(true, cfg.isAutoJoinOnStart());
        Assert.assertEquals(1, cfg.getMaxThreads());
        Assert.assertEquals(false, cfg.isStopAtShutdown());
        Assert.assertEquals(2L, cfg.getStopTimeout());
        Assert.assertEquals(3L, cfg.getIdleTimeout());
        Assert.assertEquals("localhost1", cfg.getHost());
        Assert.assertEquals(4444, cfg.getPort());
        Assert.assertEquals(5555, cfg.getSslPort());
        Assert.assertTrue(cfg.getJettyConnectors().size() == 1);
        Assert.assertTrue(cfg.getJettyConnectors().contains(JettyConnector.HTTPS));
        Assert.assertEquals(false, cfg.isRedirectWebAppsOnHttpsConnector());
        Assert.assertEquals("./privatekey1.key", cfg.getSslPrivateKeyPath());
        Assert.assertEquals("./certificate1.crt", cfg.getSslCertificatePath());
        Assert.assertEquals("./keystore1", cfg.getSslKeyStorePath());
        Assert.assertEquals("domain1", cfg.getSslKeyStoreDomainName());
        Assert.assertEquals("alias1", cfg.getSslKeyStoreAlias());
        Assert.assertEquals("pwd1", cfg.getSslKeyStorePassword());
        Assert.assertEquals(JettyKeystore.ALGORITHM_RSA, cfg.getSslKeyStoreAlgorithm());
        Assert.assertEquals(JettyKeystore.SIGNATURE_ALGORITHM_SHA256WITHRSA, cfg.getSslKeyStoreSignatureAlgorithm());
        Assert.assertEquals("rdnou1", cfg.getSslKeyStoreRdnOuValue());
        Assert.assertEquals("rdno1", cfg.getSslKeyStoreRdnOValue());
        Assert.assertEquals(10, cfg.getSslKeyStoreDateNotBeforeNumberOfDays());
        Assert.assertEquals(300, cfg.getSslKeyStoreDateNotAfterNumberOfDays());
        Assert.assertEquals(new File("/tmp1"), cfg.getTempDirectory());
        Assert.assertEquals(false, cfg.isPersistAppTempDirectories());
        Assert.assertEquals(false, cfg.isCleanTempDir());
        Assert.assertEquals(true, cfg.isParentLoaderPriority());
    }
}

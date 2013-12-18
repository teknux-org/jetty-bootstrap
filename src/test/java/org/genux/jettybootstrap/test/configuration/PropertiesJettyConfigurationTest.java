package org.genux.jettybootstrap.test.configuration;

import java.io.File;
import java.util.Properties;

import org.genux.jettybootstrap.configuration.JettyConnector;
import org.genux.jettybootstrap.configuration.PropertiesJettyConfiguration;
import org.junit.Assert;
import org.junit.Test;


public class PropertiesJettyConfigurationTest {

	@Test
	public void parseJettyConnetorsTest() {
		Assert.assertArrayEquals(null, PropertiesJettyConfiguration.parseConnectors(new String[] { "ef", "SeSL" }));
		Assert.assertArrayEquals(new JettyConnector[] { JettyConnector.SSL }, PropertiesJettyConfiguration.parseConnectors(new String[] { "ef", "SSL" }));
		Assert.assertArrayEquals(new JettyConnector[] { JettyConnector.DEFAULT }, PropertiesJettyConfiguration.parseConnectors(new String[] { "DEFAULT" }));
		Assert.assertArrayEquals(new JettyConnector[] { JettyConnector.DEFAULT, JettyConnector.SSL },
				PropertiesJettyConfiguration.parseConnectors(new String[] { "DEFAULT", "SSL" }));
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
		System.setProperty(PropertiesJettyConfiguration.KEY_CONNECTORS, "SSL");
		System.setProperty(PropertiesJettyConfiguration.KEY_REDIRECT_ALL_ON_SSL, "false");
		System.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_PASSWORD, "pwd1");
		System.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_PATH, "./keystore1");
		System.setProperty(PropertiesJettyConfiguration.KEY_TEMP_DIR, "/tmp1");
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
		Assert.assertTrue(cfg.getJettyConnectors().contains(JettyConnector.SSL));
		Assert.assertEquals(false, cfg.isRedirectAllOnSslConnector());
		Assert.assertEquals("pwd1", cfg.getSSLKeyStorePassword());
		Assert.assertEquals("./keystore1", cfg.getSSLKeyStorePath());
		Assert.assertEquals(new File("/tmp1"), cfg.getTempDirectory());
		Assert.assertEquals(true, cfg.getParentLoaderPriority());

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
		properties.setProperty(PropertiesJettyConfiguration.KEY_CONNECTORS, "DEFAULT,SSL");
		properties.setProperty(PropertiesJettyConfiguration.KEY_REDIRECT_ALL_ON_SSL, "true");
		properties.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_PASSWORD, "pwd2");
		properties.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_PATH, "./keystore2");
		properties.setProperty(PropertiesJettyConfiguration.KEY_TEMP_DIR, "/tmp2");
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
		Assert.assertTrue(cfg.getJettyConnectors().contains(JettyConnector.SSL));
		Assert.assertTrue(cfg.getJettyConnectors().contains(JettyConnector.DEFAULT));
		Assert.assertEquals(true, cfg.isRedirectAllOnSslConnector());
		Assert.assertEquals("pwd2", cfg.getSSLKeyStorePassword());
		Assert.assertEquals("./keystore2", cfg.getSSLKeyStorePath());
		Assert.assertEquals(new File("/tmp2"), cfg.getTempDirectory());
		Assert.assertEquals(false, cfg.getParentLoaderPriority());

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
		Assert.assertTrue(cfg.getJettyConnectors().contains(JettyConnector.SSL));
		Assert.assertEquals(false, cfg.isRedirectAllOnSslConnector());
		Assert.assertEquals("pwd1", cfg.getSSLKeyStorePassword());
		Assert.assertEquals("./keystore1", cfg.getSSLKeyStorePath());
		Assert.assertEquals(new File("/tmp1"), cfg.getTempDirectory());
		Assert.assertEquals(true, cfg.getParentLoaderPriority());
	}

}

package org.teknux.jettybootstrap.test.utils;

import java.util.Properties;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.teknux.jettybootstrap.configuration.PropertiesJettyConfiguration;
import org.teknux.jettybootstrap.utils.PropertiesUtils;


public class PropertiesUtilsTest {

	private static Properties testValuesProperties;

	@BeforeClass
	public static void init() {
		testValuesProperties = new Properties();

		testValuesProperties.setProperty(PropertiesJettyConfiguration.KEY_AUTO_JOIN_ON_START, "true");
		testValuesProperties.setProperty(PropertiesJettyConfiguration.KEY_STOP_AT_SHUTDOWN, "false");
		testValuesProperties.setProperty(PropertiesJettyConfiguration.KEY_PORT, "9090");
		testValuesProperties.setProperty(PropertiesJettyConfiguration.KEY_SSL_PORT, "9443");
		testValuesProperties.setProperty(PropertiesJettyConfiguration.KEY_CONNECTORS, "DEFAULT,SSL");
		testValuesProperties.setProperty(PropertiesJettyConfiguration.KEY_REDIRECT_WEBAPPS_ON_HTTPS, "false");
		testValuesProperties.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_PASSWORD, "pwd");
		testValuesProperties.setProperty(PropertiesJettyConfiguration.KEY_SSL_KEYSTORE_PATH, "./keystore");

		testValuesProperties.setProperty(PropertiesJettyConfiguration.KEY_TEMP_DIR, "/tmp");
		testValuesProperties.setProperty(PropertiesJettyConfiguration.KEY_PERSIST_APP_TEMP_DIR, "true");
		testValuesProperties.setProperty(PropertiesJettyConfiguration.KEY_CLEAN_TEMP_DIR, "false");
		testValuesProperties.setProperty(PropertiesJettyConfiguration.KEY_PARENT_LOADER_PRIORITY, "true");
	}

	@Test
	public void parseIntTest() {
		Assert.assertNull(PropertiesUtils.parseInt(null, "test"));
		Assert.assertNull(PropertiesUtils.parseInt(null, null));
		Assert.assertNull(PropertiesUtils.parseInt(new Properties(), null));
		Assert.assertEquals(new Integer(9090), PropertiesUtils.parseInt(testValuesProperties, PropertiesJettyConfiguration.KEY_PORT));
		Assert.assertEquals(new Integer(9443), PropertiesUtils.parseInt(testValuesProperties, PropertiesJettyConfiguration.KEY_SSL_PORT));
	}

	@Test
	public void parseArrayTest() {
		Assert.assertNull(PropertiesUtils.parseArray(null, null, null));
		Assert.assertNull(PropertiesUtils.parseArray(new Properties(), null, null));
		Assert.assertNull(PropertiesUtils.parseArray(new Properties(), "test", null));
		Assert.assertNull(PropertiesUtils.parseArray(new Properties(), "test", ""));
		Assert.assertNull(PropertiesUtils.parseArray(new Properties(), "test", ","));
		Assert.assertArrayEquals(new String[] { "DEFAULT", "SSL" }, PropertiesUtils.parseArray(testValuesProperties, PropertiesJettyConfiguration.KEY_CONNECTORS, ","));
	}

	@Test
	public void parseBooleanTest() {
		Assert.assertNull(PropertiesUtils.parseBoolean(null, null));
		Assert.assertNull(PropertiesUtils.parseBoolean(new Properties(), null));
		Assert.assertNull(PropertiesUtils.parseBoolean(new Properties(), "test"));

		Assert.assertEquals(Boolean.TRUE, PropertiesUtils.parseBoolean(testValuesProperties, PropertiesJettyConfiguration.KEY_AUTO_JOIN_ON_START));
		Assert.assertEquals(Boolean.FALSE, PropertiesUtils.parseBoolean(testValuesProperties, PropertiesJettyConfiguration.KEY_STOP_AT_SHUTDOWN));
	}
}

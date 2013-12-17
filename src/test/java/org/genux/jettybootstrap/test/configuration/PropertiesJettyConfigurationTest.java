package org.genux.jettybootstrap.test.configuration;

import java.util.Properties;

import org.genux.jettybootstrap.configuration.PropertiesJettyConfiguration;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class PropertiesJettyConfigurationTest {

	private static Properties testValuesProperties;

	@BeforeClass
	public static void init() {
		testValuesProperties = new Properties();

		testValuesProperties.setProperty(PropertiesJettyConfiguration.KEY_AUTO_JOIN_ON_START, "0");
		testValuesProperties.setProperty(PropertiesJettyConfiguration.KEY_CONNECTORS, "C1,C2");
	}

	@Test
	public void parseIntTest() {
		Assert.assertNull(PropertiesJettyConfiguration.parseInt(null, "test"));
		Assert.assertNull(PropertiesJettyConfiguration.parseInt(null, null));
		Assert.assertNull(PropertiesJettyConfiguration.parseInt(new Properties(), null));

		Assert.assertEquals(new Integer(0), PropertiesJettyConfiguration.parseInt(testValuesProperties, PropertiesJettyConfiguration.KEY_AUTO_JOIN_ON_START));
		Assert.assertArrayEquals(new String[] { "C1", "C2" }, PropertiesJettyConfiguration.parseArray(testValuesProperties, PropertiesJettyConfiguration.KEY_CONNECTORS, ","));
	}
}

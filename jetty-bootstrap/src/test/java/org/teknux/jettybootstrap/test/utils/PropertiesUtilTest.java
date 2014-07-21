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
package org.teknux.jettybootstrap.test.utils;

import java.util.Properties;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.teknux.jettybootstrap.configuration.PropertiesJettyConfiguration;
import org.teknux.jettybootstrap.utils.PropertiesUtil;


public class PropertiesUtilTest {

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
		Assert.assertNull(PropertiesUtil.parseInt(null, "test"));
		Assert.assertNull(PropertiesUtil.parseInt(null, null));
		Assert.assertNull(PropertiesUtil.parseInt(new Properties(), null));
		Assert.assertEquals(new Integer(9090), PropertiesUtil.parseInt(testValuesProperties, PropertiesJettyConfiguration.KEY_PORT));
		Assert.assertEquals(new Integer(9443), PropertiesUtil.parseInt(testValuesProperties, PropertiesJettyConfiguration.KEY_SSL_PORT));
	}

	@Test
	public void parseArrayTest() {
		Assert.assertNull(PropertiesUtil.parseArray(null, null, null));
		Assert.assertNull(PropertiesUtil.parseArray(new Properties(), null, null));
		Assert.assertNull(PropertiesUtil.parseArray(new Properties(), "test", null));
		Assert.assertNull(PropertiesUtil.parseArray(new Properties(), "test", ""));
		Assert.assertNull(PropertiesUtil.parseArray(new Properties(), "test", ","));
		Assert.assertArrayEquals(new String[] { "DEFAULT", "SSL" }, PropertiesUtil.parseArray(testValuesProperties, PropertiesJettyConfiguration.KEY_CONNECTORS, ","));
	}

	@Test
	public void parseBooleanTest() {
		Assert.assertNull(PropertiesUtil.parseBoolean(null, null));
		Assert.assertNull(PropertiesUtil.parseBoolean(new Properties(), null));
		Assert.assertNull(PropertiesUtil.parseBoolean(new Properties(), "test"));

		Assert.assertEquals(Boolean.TRUE, PropertiesUtil.parseBoolean(testValuesProperties, PropertiesJettyConfiguration.KEY_AUTO_JOIN_ON_START));
		Assert.assertEquals(Boolean.FALSE, PropertiesUtil.parseBoolean(testValuesProperties, PropertiesJettyConfiguration.KEY_STOP_AT_SHUTDOWN));
	}
}

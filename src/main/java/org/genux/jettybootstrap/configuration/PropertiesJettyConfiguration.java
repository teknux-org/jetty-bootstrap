package org.genux.jettybootstrap.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.genux.jettybootstrap.utils.PropertiesUtils;


/**
 * {@link IJettyConfiguration} implementation based on
 * properties. By default (empty constructor), system
 * properties are used. In addition (lower priority), a
 * {@link Properties} object can be passed to first look
 * into it for configuration values.
 * <p>
 * Partial configuration is supported since this is an
 * override of defaults.
 * 
 * @author "Francois EYL"
 */
public class PropertiesJettyConfiguration extends JettyConfiguration {

	public static final String CONNECTOR_SEPARATOR = ",";

	public static final String KEY_AUTO_JOIN_ON_START = "autoJoinOnStart";
	public static final String KEY_MAX_THREADS = "maxThreads";
	public static final String KEY_STOP_AT_SHUTDOWN = "stopAtShutdown";
	public static final String KEY_STOP_TIMEOUT = "stopTimeout";
	public static final String KEY_IDLE_TIMEOUT = "idleTimeout";
	public static final String KEY_HOST = "host";
	public static final String KEY_PORT = "port";
	public static final String KEY_SSL_PORT = "sslPort";
	public static final String KEY_CONNECTORS = "connectors";
	public static final String KEY_REDIRECT_ALL_ON_SSL = "redirectAllOnSslConnector";
	public static final String KEY_SSL_KEYSTORE_PASSWORD = "sslKeystorePassword";
	public static final String KEY_SSL_KEYSTORE_PATH = "sslKeystorePath";
	public static final String KEY_TEMP_DIR = "tempDirectory";
	public static final String KEY_PARENT_LOADER_PRIORITY = "parentLoaderPriority";

	/**
	 * Basic constructor. Only system properties are used to
	 * map jetty configuration.
	 */
	public PropertiesJettyConfiguration() {
		this(null);
	}

	/**
	 * First load the given {@link Properties} to map jetty
	 * configuration, system properties are applied after.
	 * This means system properties have higher priorities
	 * that the provided ones.
	 * 
	 * @param properties
	 */
	public PropertiesJettyConfiguration(Properties properties) {
		this(properties, false);
	}

	/**
	 * First load the given {@link Properties} to map jetty
	 * configuration, system properties are applied after.
	 * This means system properties have higher priorities
	 * that the provided ones.
	 * 
	 * @param properties
	 */
	public PropertiesJettyConfiguration(Properties properties, boolean ignoreSystemProperties) {
		if (properties != null) {
			//load given properties first
			loadProperties(properties);
		}

		if (!ignoreSystemProperties) {
			//load system properties
			loadProperties(System.getProperties());
		}
	}

	private void loadProperties(Properties properties) {
		Boolean autoJoin = PropertiesUtils.parseBoolean(properties, KEY_AUTO_JOIN_ON_START);
		if (autoJoin != null) {
			setAutoJoinOnStart(autoJoin);
		}

		Integer maxThreads = PropertiesUtils.parseInt(properties, KEY_MAX_THREADS);
		if (maxThreads != null) {
			setMaxThreads(maxThreads);
		}

		Boolean stopAtShutdown = PropertiesUtils.parseBoolean(properties, KEY_STOP_AT_SHUTDOWN);
		if (stopAtShutdown != null) {
			setStopAtShutdown(stopAtShutdown);
		}

		Long stopTimeout = PropertiesUtils.parseLong(properties, KEY_STOP_TIMEOUT);
		if (stopTimeout != null) {
			setStopTimeout(stopTimeout);
		}

		Long idleTimeout = PropertiesUtils.parseLong(properties, KEY_IDLE_TIMEOUT);
		if (idleTimeout != null) {
			setIdleTimeout(idleTimeout);
		}

		String host = properties.getProperty(KEY_HOST);
		if (host != null) {
			setHost(host);
		}

		Integer port = PropertiesUtils.parseInt(properties, KEY_PORT);
		if (port != null) {
			setPort(port);
		}

		Integer sslPort = PropertiesUtils.parseInt(properties, KEY_SSL_PORT);
		if (sslPort != null) {
			setSslPort(sslPort);
		}

		JettyConnector[] connectors = parseConnectors(PropertiesUtils.parseArray(properties, KEY_CONNECTORS, CONNECTOR_SEPARATOR));
		if (connectors != null) {
			setJettyConnectors(connectors);
		}

		Boolean redirectOnSSL = PropertiesUtils.parseBoolean(properties, KEY_REDIRECT_ALL_ON_SSL);
		if (redirectOnSSL != null) {
			setRedirectAllOnSslConnector(redirectOnSSL);
		}

		String sslKeystorePath = properties.getProperty(KEY_SSL_KEYSTORE_PATH);
		if (sslKeystorePath != null) {
			setSSLKeyStorePath(sslKeystorePath);
		}

		String sslKeystorePwd = properties.getProperty(KEY_SSL_KEYSTORE_PASSWORD);
		if (sslKeystorePwd != null) {
			setSSLKeyStorePassword(sslKeystorePwd);
		}

		String tempDir = properties.getProperty(KEY_TEMP_DIR);
		if (tempDir != null) {
			setTempDirectory(new File(tempDir));
		}

		Boolean parentLoaderPriority = PropertiesUtils.parseBoolean(properties, KEY_PARENT_LOADER_PRIORITY);
		if (parentLoaderPriority != null) {
			setParentLoaderPriority(parentLoaderPriority);
		}
	}

	/**
	 * Parse the given array of String and return an array
	 * of {@link JettyConnector}. Invalid entry from input
	 * array are ignored, returns <code>null</code> when no
	 * value match from input array.
	 * 
	 * @param connectors
	 * @return array of {@link JettyConnector}
	 *         <code>null</code> in case nothing match.
	 */
	public static JettyConnector[] parseConnectors(String[] connectors) {
		if (connectors == null) {
			return null;
		}

		List<JettyConnector> array = new ArrayList<JettyConnector>();
		for (String coonectorString : connectors) {
			try {
				array.add(JettyConnector.valueOf(coonectorString));
			} catch (IllegalArgumentException e) {
				//nothing to do here
			}

		}

		return !array.isEmpty() ? array.toArray(new JettyConnector[array.size()]) : null;
	}
}

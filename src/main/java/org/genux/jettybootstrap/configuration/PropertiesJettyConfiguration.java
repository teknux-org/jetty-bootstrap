package org.genux.jettybootstrap.configuration;

import java.util.Properties;


public class PropertiesJettyConfiguration extends JettyConfiguration {

	public static final String KEY_AUTO_JOIN_ON_START = "autoJoinOnStart";
	public static final String KEY_MAX_THREADS = "maxThreads";
	public static final String KEY_STOP_AT_SHUTDOWN = "stopAtShutdown";
	public static final String KEY_STOP_TIMEOUT = "stopTimeout";
	public static final String KEY_IDLE_TIMEOUT = "idleTimeout";
	public static final String KEY_HOST = "host";
	public static final String KEY_PORT = "port";
	public static final String KEY_SSL_PORT = "sslPort";
	public static final String KEY_CONNECTORS = "connectors";
	public static final String KEY_SSL_KEYSTORE_PASSWORD = "sslKeystorePassword";
	public static final String KEY_SSL_KEYSTORE_PATH = "sslKeystorePath";
	public static final String KEY_TEMP_DIR = "tempDirectory";
	public static final String KEY_PARENT_LOADER_PRIORITY = "parentLoaderPriority";

	public PropertiesJettyConfiguration() {
		//TODO: implement System.getProperty in order to load configuration

	}

	public PropertiesJettyConfiguration(Properties properties) {
		this();
		//TODO: implement load configuration from given Properties to override defaults and system properties

	}

	public static Integer parseInt(Properties p, String key) {
		if (p == null || key == null) {
			return null;
		}

		String value = p.getProperty(key);
		if (value == null) {
			return null;
		}

		return Integer.parseInt(value);
	}

	public static String[] parseArray(Properties p, String key, String separator) {
		if (p == null || key == null || separator == null || separator.isEmpty()) {
			return null;
		}

		String value = p.getProperty(key);
		if (value == null) {
			return null;
		}

		return value.contains(separator) ? value.split(separator) : new String[] { value };
	}
}

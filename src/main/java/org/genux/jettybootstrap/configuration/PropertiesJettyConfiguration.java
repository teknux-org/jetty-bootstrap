package org.genux.jettybootstrap.configuration;

import java.util.Properties;



public class PropertiesJettyConfiguration extends JettyConfiguration {
	
	
	public PropertiesJettyConfiguration() {
		//TODO: implement System.getProperty in order to load configuration
	}
	
	public PropertiesJettyConfiguration(Properties properties) {
		this();
		//TODO: implement load configuration from given Properties to override defaults and system propeties
	}
}

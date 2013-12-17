package org.genux.jettybootstrap;

import java.io.File;


public interface IJettyConfiguration {

	boolean isStopOnShutdownHook();

	void setStopOnShutdownHook(boolean stopOnShutdownHook);

	boolean isAutoJoinOnStart();

	void setAutoJoinOnStart(boolean autoJoinOnStart);

	int getMaxThreads();

	void setMaxThreads(int maxThreads);

	boolean isStopAtShutdown();

	//	void setStopAtShutdown(boolean stopAtShutdown);

	long getStopTimeout();

	void setStopTimeout(long stopTimeout);

	long getIdleTimeout();

	void setIdleTimeout(long idleTimeout);

	String getHost();

	void setHost(String host);

	int getPort();

	void setPort(int port);

	int getSslPort();

	void setSslPort(int sslPort);

	JettyConnectors getJettyConnectors();

	void setJettyConnectors(JettyConnectors jettyConnectors);

	String getSSLKeyStorePassword();

	void setSSLKeyStorePassword(String sSLKeyStorePassword);

	String getSSLKeyStorePath();

	void setSSLKeyStorePath(String sSLKeyStorePath);

	File getTempDirectory();

	void setTempDirectory(File tempDirectory);

	boolean getParentLoaderPriority();

	void setParentLoaderPriority(boolean parentLoaderPriority);

}

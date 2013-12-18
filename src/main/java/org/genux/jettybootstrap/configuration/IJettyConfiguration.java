package org.genux.jettybootstrap.configuration;

import java.io.File;
import java.util.Set;



public interface IJettyConfiguration {

	boolean isAutoJoinOnStart();

	void setAutoJoinOnStart(boolean autoJoinOnStart);

	int getMaxThreads();

	void setMaxThreads(int maxThreads);

	boolean isStopAtShutdown();

	void setStopAtShutdown(boolean stopAtShutdown);

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

	boolean hasJettyConnector(JettyConnector jettyConnector);

	Set<JettyConnector> getJettyConnectors();

	void setJettyConnectors(JettyConnector... jettyConnectors);

	boolean isRedirectAllOnSslConnector();

	void setRedirectAllOnSslConnector(boolean redirectAllOnSslConnector);

	String getSSLKeyStorePassword();

	void setSSLKeyStorePassword(String sSLKeyStorePassword);

	String getSSLKeyStorePath();

	void setSSLKeyStorePath(String sSLKeyStorePath);

	File getTempDirectory();

	void setTempDirectory(File tempDirectory);

	boolean isDeleteTempDirAtShutdown();

	void setDeleteTempDirAtShutdown(boolean deleteTempDirAtShutdown);

	boolean getParentLoaderPriority();

	void setParentLoaderPriority(boolean parentLoaderPriority);
}

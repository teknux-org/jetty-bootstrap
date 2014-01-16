package org.genux.jettybootstrap.configuration;

import java.io.File;
import java.util.Set;


/**
 * This interface represents all the available configuration options for the jetty server
 */
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

	boolean isRedirectWebAppsOnHttpsConnector();

	void setRedirectWebAppsOnHttpsConnector(boolean redirectWebAppsOnHttpsConnector);

	String getSslKeyStorePassword();

	void setSslKeyStorePassword(String sslKeyStorePassword);

	String getSslKeyStorePath();

	void setSslKeyStorePath(String sslKeyStorePath);

	File getTempDirectory();

	void setTempDirectory(File tempDirectory);

	boolean isPersistAppTempDirectories();

	void setPersistAppTempDirectories(boolean persistTempDirectory);

	boolean isCleanTempDir();

	void setCleanTempDir(boolean cleanTempDir);

	boolean isParentLoaderPriority();

	void setParentLoaderPriority(boolean parentLoaderPriority);
}

package org.teknux.jettybootstrap.configuration;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * Base implementation of the configuration properties holding default values.
 */
public class JettyConfiguration implements
		IJettyConfiguration {

	private boolean autoJoinOnStart = true;

	private int maxThreads = 128;
	private boolean stopAtShutdown = true;
	private long stopTimeout = 5000;

	private long idleTimeout = 30000;
	private String host = "0.0.0.0";
	private int port = 8080;
	private int sslPort = 8443;

	private Set<JettyConnector> jettyConnectors = new HashSet<JettyConnector>(Arrays.asList(JettyConnector.HTTP));
	private boolean redirectWebAppsOnHttpsConnector = false;
	private String sslKeyStorePassword = null;
	private String sslKeyStorePath = null;

	private File tempDirectory = null;
	private boolean persistAppTempDirectories = false;
	private boolean cleanTempDir = false;
	private boolean parentLoaderPriority = true;

	@Override
	public boolean isAutoJoinOnStart() {
		return autoJoinOnStart;
	}

	@Override
	public void setAutoJoinOnStart(boolean autoJoinOnStart) {
		this.autoJoinOnStart = autoJoinOnStart;
	}

	@Override
	public int getMaxThreads() {
		return maxThreads;
	}

	@Override
	public void setMaxThreads(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	@Override
	public boolean isStopAtShutdown() {
		return stopAtShutdown;
	}

	@Override
	public void setStopAtShutdown(boolean stopAtShutdown) {
		this.stopAtShutdown = stopAtShutdown;
	}

	@Override
	public long getStopTimeout() {
		return stopTimeout;
	}

	@Override
	public void setStopTimeout(long stopTimeout) {
		this.stopTimeout = stopTimeout;
	}

	@Override
	public long getIdleTimeout() {
		return idleTimeout;
	}

	@Override
	public void setIdleTimeout(long idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public int getSslPort() {
		return sslPort;
	}

	@Override
	public void setSslPort(int sslPort) {
		this.sslPort = sslPort;
	}

	@Override
	public boolean hasJettyConnector(JettyConnector jettyConnector) {
		return (jettyConnectors.contains(jettyConnector));
	}

	@Override
	public Set<JettyConnector> getJettyConnectors() {
		return jettyConnectors;
	}

	@Override
	public void setJettyConnectors(JettyConnector... jettyConnectors) {
		this.jettyConnectors = new HashSet<JettyConnector>(Arrays.asList(jettyConnectors));
	}

	@Override
	public boolean isRedirectWebAppsOnHttpsConnector() {
		return redirectWebAppsOnHttpsConnector;
	}

	@Override
	public void setRedirectWebAppsOnHttpsConnector(boolean redirectWebAppsOnHttpsConnector) {
		this.redirectWebAppsOnHttpsConnector = redirectWebAppsOnHttpsConnector;
	}

	@Override
	public String getSslKeyStorePassword() {
		return sslKeyStorePassword;
	}

	@Override
	public void setSslKeyStorePassword(String sslKeyStorePassword) {
		this.sslKeyStorePassword = sslKeyStorePassword;
	}

	@Override
	public String getSslKeyStorePath() {
		return sslKeyStorePath;
	}

	@Override
	public void setSslKeyStorePath(String sslKeyStorePath) {
		this.sslKeyStorePath = sslKeyStorePath;
	}

	@Override
	public File getTempDirectory() {
		return tempDirectory;
	}

	@Override
	public void setTempDirectory(File tempDirectory) {
		this.tempDirectory = tempDirectory;
	}

	@Override
	public boolean isPersistAppTempDirectories() {
		return persistAppTempDirectories;
	}

	@Override
	public void setPersistAppTempDirectories(boolean persistAppTempDirectories) {
		this.persistAppTempDirectories = persistAppTempDirectories;
	}

	@Override
	public boolean isCleanTempDir() {
		return cleanTempDir;
	}

	@Override
	public void setCleanTempDir(boolean cleanTempDir) {
		this.cleanTempDir = cleanTempDir;
	}

	@Override
	public boolean isParentLoaderPriority() {
		return parentLoaderPriority;
	}

	@Override
	public void setParentLoaderPriority(boolean parentLoaderPriority) {
		this.parentLoaderPriority = parentLoaderPriority;
	}

	@Override
	public String toString() {
		return "JettyConfiguration [autoJoinOnStart=" + autoJoinOnStart + ", maxThreads=" + maxThreads + ", stopAtShutdown=" + stopAtShutdown + ", stopTimeout=" + stopTimeout +
			", idleTimeout=" + idleTimeout + ", host=" + host + ", port=" + port + ", sslPort=" + sslPort + ", jettyConnectors=" + jettyConnectors +
			", redirectWebAppsOnHttpsConnector=" + redirectWebAppsOnHttpsConnector + ", sslKeyStorePassword=" + sslKeyStorePassword + ", sslKeyStorePath=" + sslKeyStorePath +
			", tempDirectory=" + tempDirectory + ", persistAppTempDirectories=" + persistAppTempDirectories + ", cleanTempDir=" + cleanTempDir + ", parentLoaderPriority=" +
			parentLoaderPriority + "]";
	}
}

package org.genux.jettybootstrap;

import java.io.File;


public class JettyConfiguration implements
		IJettyConfiguration {

	private boolean stopOnShutdownHook = true;
	private boolean autoJoinOnStart = true;

	private int maxThreads = 128;
	private boolean stopAtShutdown = false;
	private long stopTimeout = 5000;

	private long idleTimeout = 30000;
	private String host = "localhost";
	private int port = 8080;
	private int sslPort = 8443;

	private JettyConnectors jettyConnectors = JettyConnectors.DEFAULT;
	private String SSLKeyStorePassword = null;
	private String SSLKeyStorePath = null;

	private File tempDirectory = null;
	private boolean parentLoaderPriority = true;

	@Override
	public boolean isStopOnShutdownHook() {
		return stopOnShutdownHook;
	}

	@Override
	public void setStopOnShutdownHook(boolean stopOnShutdownHook) {
		this.stopOnShutdownHook = stopOnShutdownHook;
	}

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

	//	Not permit to change this native jetty param because shutdown has been reimplemented
	//	Use @setStopOnShutdownHook instead
	//	@Override
	//	public void setStopAtShutdown(boolean stopAtShutdown) {
	//		this.stopAtShutdown = stopAtShutdown;
	//	}

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
	public JettyConnectors getJettyConnectors() {
		return jettyConnectors;
	}

	@Override
	public void setJettyConnectors(JettyConnectors jettyConnectors) {
		this.jettyConnectors = jettyConnectors;
	}

	@Override
	public String getSSLKeyStorePassword() {
		return SSLKeyStorePassword;
	}

	@Override
	public void setSSLKeyStorePassword(String sSLKeyStorePassword) {
		SSLKeyStorePassword = sSLKeyStorePassword;
	}

	@Override
	public String getSSLKeyStorePath() {
		return SSLKeyStorePath;
	}

	@Override
	public void setSSLKeyStorePath(String sSLKeyStorePath) {
		SSLKeyStorePath = sSLKeyStorePath;
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
	public boolean getParentLoaderPriority() {
		return parentLoaderPriority;
	}

	@Override
	public void setParentLoaderPriority(boolean parentLoaderPriority) {
		this.parentLoaderPriority = parentLoaderPriority;
	}

	@Override
	public String toString() {
		return "JettyConfiguration [stopOnShutdownHook=" + stopOnShutdownHook + ", autoJoinOnStart=" + autoJoinOnStart + ", maxThreads=" + maxThreads + ", stopAtShutdown=" +
			stopAtShutdown + ", stopTimeout=" + stopTimeout + ", idleTimeout=" + idleTimeout + ", host=" + host + ", port=" + port + ", sslPort=" + sslPort + ", jettyConnectors=" +
			jettyConnectors + ", SSLKeyStorePassword=" + SSLKeyStorePassword + ", SSLKeyStorePath=" + SSLKeyStorePath + ", tempDirectory=" + tempDirectory +
			", parentLoaderPriority=" + parentLoaderPriority + "]";
	}
}

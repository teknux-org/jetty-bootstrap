package org.genux.jettybootstrap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.genux.jettybootstrap.configuration.IJettyConfiguration;
import org.genux.jettybootstrap.configuration.JettyConnector;
import org.genux.jettybootstrap.configuration.PropertiesJettyConfiguration;
import org.genux.jettybootstrap.handler.IJettyHandler;
import org.genux.jettybootstrap.handler.JettyHandler;
import org.genux.jettybootstrap.handler.WebAppResourceWarJettyHandler;
import org.genux.jettybootstrap.handler.WebAppWarJettyHandler;
import org.genux.jettybootstrap.keystore.JettyKeystore;
import org.genux.jettybootstrap.keystore.JettyKeystoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JettyBootstrap {

	private final Logger logger = LoggerFactory.getLogger(JettyBootstrap.class);

	private static final String DEFAULT_KEYSTORE_FILENAME = "default.keystore";
	private static final String DEFAULT_KEYSTORE_DOMAINNAME = "unknown";
	private static final String DEFAULT_KEYSTORE_ALIAS = "jettybootstrap";
	private static final String DEFAULT_KEYSTORE_PASSWORD = "jettybootstrap";

	private static final String TEMP_DIRECTORY_NAME = ".temp";
	public static final File TEMP_DIRECTORY_JARDIR = new File(getJarDir().getPath() + File.separator + TEMP_DIRECTORY_NAME);
	public static final File TEMP_DIRECTORY_SYSTEMP = new File(System.getProperty("java.io.tmpdir") + File.separator + TEMP_DIRECTORY_NAME);
	protected static final File TEMP_DIRECTORY_DEFAULT = TEMP_DIRECTORY_JARDIR;

	private static final String CONTEXT_PATH_ROOT = "/";

	private IJettyConfiguration iJettyConfiguration;
	private List<IJettyHandler> jettyHandlers = new ArrayList<IJettyHandler>();

	private Server server = null;
	private HandlerList handlers = new HandlerList();

	public JettyBootstrap() {
		this(new PropertiesJettyConfiguration());
	}

	public JettyBootstrap(IJettyConfiguration iJettyConfiguration) {
		this.iJettyConfiguration = iJettyConfiguration;
	}

	/**
	 * Start Jetty
	 * 
	 * @return
	 * @throws JettyBootstrapException
	 */
	public JettyBootstrap startJetty() throws JettyBootstrapException {
		return startJetty(iJettyConfiguration.isAutoJoinOnStart());
	}

	/**
	 * Start Jetty
	 * 
	 * @param join
	 * @return
	 * @throws JettyBootstrapException
	 */
	public JettyBootstrap startJetty(boolean join) throws JettyBootstrapException {
		if (server == null) {
			init(iJettyConfiguration);
		}
		setHandlers();

		logger.info("Start Jetty...");
		try {
			server.start();
		} catch (Exception e) {
			throw new JettyBootstrapException(e);
		}

		if (join) {
			joinJetty();
		}

		return this;
	}

	/**
	 * Join Jetty
	 * 
	 * @return
	 * @throws JettyBootstrapException
	 */
	public JettyBootstrap joinJetty() throws JettyBootstrapException {
		try {
			if (server != null && server.isStarted()) {
				logger.debug("Join Jetty...");

				server.join();
			} else {
				logger.warn("Can't join Jetty. Not started");
			}
		} catch (InterruptedException e) {
			throw new JettyBootstrapException(e);
		}

		return this;
	}

	/**
	 * Stop Jetty
	 * 
	 * @return
	 * @throws JettyBootstrapException
	 */
	public JettyBootstrap stopJetty() throws JettyBootstrapException {
		try {
			handlers.stop();

			if (server != null && server.isStarted()) {
				logger.info("Stop Jetty...");

				server.stop();
			} else {
				logger.warn("Can't stop Jetty. Already stopped");
			}
		} catch (Exception e) {
			throw new JettyBootstrapException(e);
		}

		return this;
	}

	/**
	 * Add War
	 * 
	 * @param warFile
	 * @return
	 */
	public JettyBootstrap addWar(File warFile) {
		return addWar(warFile, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add War
	 * 
	 * @param warFile
	 * @param contextPath
	 * @return
	 */
	public JettyBootstrap addWar(File warFile, String contextPath) {
		WebAppWarJettyHandler webAppWarJettyHandler = new WebAppWarJettyHandler();
		webAppWarJettyHandler.setWarFile(warFile);
		webAppWarJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(webAppWarJettyHandler);

		return this;
	}

	/**
	 * Add ResourceWar
	 * 
	 * @param resource
	 * @return
	 */
	public JettyBootstrap addResourceWar(String resource) {
		return addResourceWar(resource, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add ResourceWar
	 * 
	 * @param resource
	 * @param contextPath
	 * @return
	 */
	public JettyBootstrap addResourceWar(String resource, String contextPath) {
		WebAppResourceWarJettyHandler webAppResourceWarJettyHandler = new WebAppResourceWarJettyHandler();
		webAppResourceWarJettyHandler.setResource(resource);
		webAppResourceWarJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(webAppResourceWarJettyHandler);

		return this;
	}

	/**
	 * Add Handler
	 * 
	 * @param handler
	 * @return
	 */
	public JettyBootstrap addHandler(Handler handler) {
		JettyHandler jettyHandler = new JettyHandler();
		jettyHandler.setHandler(handler);

		jettyHandlers.add(jettyHandler);

		return this;
	}

	/**
	 * Get Jetty Server Object
	 * 
	 * @return
	 * @throws JettyBootstrapException
	 */
	public Server getServer() throws JettyBootstrapException {
		if (server == null) {
			init(iJettyConfiguration);
		}
		return server;
	}

	protected void init(IJettyConfiguration iJettyConfiguration) throws JettyBootstrapException {
		this.iJettyConfiguration = initConfiguration(iJettyConfiguration);

		server = createServer(iJettyConfiguration);
		server.setConnectors(createConnectors(iJettyConfiguration, server));

		server.setHandler(handlers);

		createShutdownHook(iJettyConfiguration);
	}

	protected IJettyConfiguration initConfiguration(IJettyConfiguration iJettyConfiguration) throws JettyBootstrapException {
		logger.debug("Init Configuration...");

		logger.trace("Check Temp Directory...");
		if (iJettyConfiguration.getTempDirectory() == null) {
			iJettyConfiguration.setTempDirectory(TEMP_DIRECTORY_DEFAULT);
		}

		if (iJettyConfiguration.getTempDirectory().exists() && iJettyConfiguration.isCleanTempDir()) {
			logger.trace("Clean Temp Directory...");

			try {
				FileUtils.deleteDirectory(iJettyConfiguration.getTempDirectory());
			} catch (IOException e) {
				throw new JettyBootstrapException("Can't clean temporary directory");
			}
		}
		if (!iJettyConfiguration.getTempDirectory().exists() && !iJettyConfiguration.getTempDirectory().mkdirs()) {
			throw new JettyBootstrapException("Can't create temporary directory");
		}

		logger.trace("Check required properties...");
		if (iJettyConfiguration.getHost() == null || iJettyConfiguration.getHost().isEmpty()) {
			throw new JettyBootstrapException("Host not specified");
		}

		logger.trace("Check connectors...");
		if (iJettyConfiguration.hasJettyConnector(JettyConnector.HTTPS) && (iJettyConfiguration.getSSLKeyStorePath() == null || iJettyConfiguration.getSSLKeyStorePath().isEmpty())) {
			File keystoreFile = new File(iJettyConfiguration.getTempDirectory().getPath() + File.separator + DEFAULT_KEYSTORE_FILENAME);

			if (!keystoreFile.exists()) {
				try {
					JettyKeystore.generateKeystoreAndSave(DEFAULT_KEYSTORE_DOMAINNAME, DEFAULT_KEYSTORE_ALIAS, DEFAULT_KEYSTORE_PASSWORD, keystoreFile);
				} catch (JettyKeystoreException e) {
					throw new JettyBootstrapException("Can't generate keyStore", e);
				}
			}
			iJettyConfiguration.setSSLKeyStorePath(keystoreFile.getPath());
			iJettyConfiguration.setSSLKeyStorePassword(DEFAULT_KEYSTORE_PASSWORD);
		}

		if (iJettyConfiguration.isRedirectWebAppsOnHttpsConnector() &&
			(!iJettyConfiguration.hasJettyConnector(JettyConnector.HTTP) || !iJettyConfiguration.hasJettyConnector(JettyConnector.HTTPS))) {
			throw new JettyBootstrapException("You can't redirect all from HTTP to HTTPS Connector if both connectors are not setted");
		}

		logger.trace("Configuration : {}", iJettyConfiguration);

		return iJettyConfiguration;
	}

	protected Server createServer(IJettyConfiguration iJettyConfiguration) {
		logger.trace("Create Jetty Server...");

		Server server = new Server(new QueuedThreadPool(iJettyConfiguration.getMaxThreads()));
		server.setStopAtShutdown(false); //Reimplemented. See @IJettyConfiguration.stopAtShutdown
		server.setStopTimeout(iJettyConfiguration.getStopTimeout());

		return server;
	}

	protected Connector[] createConnectors(IJettyConfiguration iJettyConfiguration, Server server) {
		logger.trace("Create Jetty Connectors...");

		List<Connector> connectors = new ArrayList<Connector>();

		if (iJettyConfiguration.hasJettyConnector(JettyConnector.HTTP)) {
			logger.trace("Add HTTP Connector...");

			ServerConnector serverConnector;

			if (iJettyConfiguration.hasJettyConnector(JettyConnector.HTTPS)) {
				HttpConfiguration httpConfiguration = new HttpConfiguration();
				httpConfiguration.setSecurePort(iJettyConfiguration.getSslPort());
				httpConfiguration.setSecureScheme(HttpScheme.HTTPS.asString());

				HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration);

				serverConnector = new ServerConnector(server, httpConnectionFactory);
			} else {
				serverConnector = new ServerConnector(server);
			}
			serverConnector.setIdleTimeout(iJettyConfiguration.getIdleTimeout());
			serverConnector.setHost(iJettyConfiguration.getHost());
			serverConnector.setPort(iJettyConfiguration.getPort());

			connectors.add(serverConnector);
		}
		if (iJettyConfiguration.hasJettyConnector(JettyConnector.HTTPS)) {
			logger.trace("Add HTTPS Connector...");

			SslContextFactory sslContextFactory = new SslContextFactory(iJettyConfiguration.getSSLKeyStorePath());
			sslContextFactory.setKeyStorePassword(iJettyConfiguration.getSSLKeyStorePassword());
			ServerConnector serverConnector = new ServerConnector(server, sslContextFactory);

			serverConnector.setIdleTimeout(iJettyConfiguration.getIdleTimeout());
			serverConnector.setHost(iJettyConfiguration.getHost());
			serverConnector.setPort(iJettyConfiguration.getSslPort());

			connectors.add(serverConnector);
		}

		return connectors.toArray(new Connector[connectors.size()]);
	}

	protected void shutdown(IJettyConfiguration iJettyConfiguration) {
		try {
			logger.debug("Shutdown...");
			if (iJettyConfiguration.isStopAtShutdown()) {
				stopJetty();
			}
		} catch (Exception e) {
			logger.error("Shutdown", e);
		}
	}

	/**
	 * Set Handlers to jetty
	 * 
	 * @throws JettyBootstrapException
	 */
	private void setHandlers() throws JettyBootstrapException {
		if (jettyHandlers.size() == 0) {
			return;
		}

		handlers.removeBeans();

		for (IJettyHandler jettyHandler : jettyHandlers) {
			if (jettyHandler instanceof WebAppWarJettyHandler) {
				WebAppWarJettyHandler webAppWarJettyHandler = (WebAppWarJettyHandler) jettyHandler;
				webAppWarJettyHandler.setTempDirectory(iJettyConfiguration.getTempDirectory());
				webAppWarJettyHandler.setPersistTempDirectory(iJettyConfiguration.isPersistAppTempDirectories());
				webAppWarJettyHandler.setRedirectOnHttpsConnector(iJettyConfiguration.isRedirectWebAppsOnHttpsConnector());
			}

			handlers.addHandler(jettyHandler.getHandler());
		}
	}

	/**
	 * Create Shutdown Hook.
	 * 
	 * @param iJettyConfiguration
	 */
	private void createShutdownHook(final IJettyConfiguration iJettyConfiguration) {
		logger.trace("Create Jetty ShutdownHook...");

		Runtime.getRuntime().addShutdownHook(new Thread() {

			public void run() {
				shutdown(iJettyConfiguration);
			}
		});
	}

	/**
	 * Get directory location of Jar
	 * 
	 * @return @File
	 */
	private static File getJarDir() {
		return new File(JettyBootstrap.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
	}
}

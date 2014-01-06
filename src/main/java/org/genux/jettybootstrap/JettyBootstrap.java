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
import org.genux.jettybootstrap.handler.AbstractWebAppJettyHandler;
import org.genux.jettybootstrap.handler.IJettyHandler;
import org.genux.jettybootstrap.handler.JettyHandler;
import org.genux.jettybootstrap.handler.WebAppJettyHandler;
import org.genux.jettybootstrap.handler.WebAppResourceWarJettyHandler;
import org.genux.jettybootstrap.handler.WebAppStaticContentJettyHandler;
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

	private static final String RESOURCE_WEBAPP = "/webapp";
	private static final String CONTEXT_PATH_ROOT = "/";

	private IJettyConfiguration iJettyConfiguration;
	private List<IJettyHandler> jettyHandlers = new ArrayList<IJettyHandler>();

	private Server server = null;
	private HandlerList handlers = new HandlerList();

	/**
	 * Quick Start
	 * 
	 * @return
	 * @throws JettyBootstrapException
	 */
	public static JettyBootstrap startSelf() throws JettyBootstrapException {
		return new JettyBootstrap().addSelf().startServer();
	}

	public JettyBootstrap() {
		this(new PropertiesJettyConfiguration());
	}

	public JettyBootstrap(IJettyConfiguration iJettyConfiguration) {
		this.iJettyConfiguration = iJettyConfiguration;
	}

	/**
	 * Start Jetty Server
	 * 
	 * @return
	 * @throws JettyBootstrapException
	 */
	public JettyBootstrap startServer() throws JettyBootstrapException {
		return startServer(iJettyConfiguration.isAutoJoinOnStart());
	}

	/**
	 * Start Jetty Server
	 * 
	 * @param join
	 * @return
	 * @throws JettyBootstrapException
	 */
	public JettyBootstrap startServer(boolean join) throws JettyBootstrapException {
		if (server == null) {
			init(iJettyConfiguration);
		}
		setHandlers();

		logger.info("Start Jetty Server...");
		try {
			server.start();
		} catch (Exception e) {
			throw new JettyBootstrapException(e);
		}

		if (join) {
			joinServer();
		}

		return this;
	}

	/**
	 * Join Jetty Server
	 * 
	 * @return
	 * @throws JettyBootstrapException
	 */
	public JettyBootstrap joinServer() throws JettyBootstrapException {
		try {
			if (server != null && server.isStarted()) {
				logger.debug("Join Jetty Server...");

				server.join();
			} else {
				logger.warn("Can't join Jetty Server. Not started");
			}
		} catch (InterruptedException e) {
			throw new JettyBootstrapException(e);
		}

		return this;
	}

	/**
	 * Stop Jetty Server
	 * 
	 * @return
	 * @throws JettyBootstrapException
	 */
	public JettyBootstrap stopServer() throws JettyBootstrapException {
		try {
			handlers.stop();

			if (server != null && server.isStarted()) {
				logger.info("Stop Jetty Server...");

				server.stop();
			} else {
				logger.warn("Can't stop Jetty Server. Already stopped");
			}
		} catch (Exception e) {
			throw new JettyBootstrapException(e);
		}

		return this;
	}

	/**
	 * Add War Application in Root Context
	 * 
	 * @param warFile
	 * @return
	 */
	public JettyBootstrap addWar(String warFile) {
		return addWar(warFile, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add War Application
	 * 
	 * @param warFile
	 * @param contextPath
	 * @return
	 */
	public JettyBootstrap addWar(String warFile, String contextPath) {
		WebAppWarJettyHandler webAppWarJettyHandler = new WebAppWarJettyHandler();
		webAppWarJettyHandler.setWarFile(warFile);
		webAppWarJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(webAppWarJettyHandler);

		return this;
	}

	/**
	 * Add War Application from Resource in Root Context
	 * 
	 * @param resourceWar
	 * @return
	 */
	public JettyBootstrap addResourceWar(String resourceWar) {
		return addResourceWar(resourceWar, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add War Application from Resource
	 * 
	 * @param resourceWar
	 * @param contextPath
	 * @return
	 */
	public JettyBootstrap addResourceWar(String resourceWar, String contextPath) {
		WebAppResourceWarJettyHandler webAppResourceWarJettyHandler = new WebAppResourceWarJettyHandler();
		webAppResourceWarJettyHandler.setResourceWar(resourceWar);
		webAppResourceWarJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(webAppResourceWarJettyHandler);

		return this;
	}

	/**
	 * Add Static Application from Directory in Root Context
	 * 
	 * @param webAppBase
	 * @return
	 */
	public JettyBootstrap addStaticContent(String webAppBase) {
		return addStaticContent(webAppBase, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add Static Application from Directory
	 * 
	 * @param webAppBase
	 * @param contextPath
	 * @return
	 */
	public JettyBootstrap addStaticContent(String webAppBase, String contextPath) {
		WebAppStaticContentJettyHandler webAppStaticJettyHandler = new WebAppStaticContentJettyHandler();
		webAppStaticJettyHandler.setWebAppBase(webAppBase);
		webAppStaticJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(webAppStaticJettyHandler);

		return this;
	}

	/**
	 * Add Static Application from Resource Directory in
	 * Root Context
	 * 
	 * @param webAppResourceBase
	 * @return
	 */
	public JettyBootstrap addResourceStaticContent(String webAppResourceBase) {
		return addResourceStaticContent(webAppResourceBase, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add Static Application from Resource Directory
	 * 
	 * @param webAppResourceBase
	 * @param contextPath
	 * @return
	 */
	public JettyBootstrap addResourceStaticContent(String webAppResourceBase, String contextPath) {
		WebAppStaticContentJettyHandler webAppStaticJettyHandler = new WebAppStaticContentJettyHandler();
		webAppStaticJettyHandler.setWebAppResourceBase(webAppResourceBase);
		webAppStaticJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(webAppStaticJettyHandler);

		return this;
	}

	/**
	 * Add Application from Directory in Root Context
	 * 
	 * @param webAppBase
	 * @param descriptor
	 * @return
	 */
	public JettyBootstrap add(String webAppBase, String descriptor) {
		return add(webAppBase, descriptor, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add Application from Directory
	 * 
	 * @param webAppBase
	 * @param descriptor
	 * @param contextPath
	 * @return
	 */
	public JettyBootstrap add(String webAppBase, String descriptor, String contextPath) {
		WebAppJettyHandler webAppJettyHandler = new WebAppJettyHandler();
		webAppJettyHandler.setWebAppBase(webAppBase);
		webAppJettyHandler.setDescriptor(descriptor);
		webAppJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(webAppJettyHandler);

		return this;
	}

	/**
	 * Add Application from Resource Directory
	 * 
	 * @param webAppResourceBase
	 * @param descriptor
	 * @return
	 */
	public JettyBootstrap addResource(String webAppResourceBase, String descriptor) {
		return addResource(webAppResourceBase, descriptor, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add Application from Resource Directory in Root
	 * Context
	 * 
	 * @param webAppResourceBase
	 * @param descriptor
	 * @param contextPath
	 * @return
	 */
	public JettyBootstrap addResource(String webAppResourceBase, String descriptor, String contextPath) {
		WebAppJettyHandler webAppJettyHandler = new WebAppJettyHandler();
		webAppJettyHandler.setWebAppResourceBase(webAppResourceBase);
		webAppJettyHandler.setDescriptor(descriptor);
		webAppJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(webAppJettyHandler);

		return this;
	}

	/**
	 * Add Application from Myself in Root Context
	 * 
	 * @return
	 */
	public JettyBootstrap addSelf() {
		return addResource(RESOURCE_WEBAPP, null);
	}

	/**
	 * Add Application from Myself
	 * 
	 * @param contextPath
	 * @return
	 */
	public JettyBootstrap addSelf(String contextPath) {
		return addResource(RESOURCE_WEBAPP, null, contextPath);
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
		if (iJettyConfiguration.hasJettyConnector(JettyConnector.HTTPS) && (iJettyConfiguration.getSslKeyStorePath() == null || iJettyConfiguration.getSslKeyStorePath().isEmpty())) {
			File keystoreFile = new File(iJettyConfiguration.getTempDirectory().getPath() + File.separator + DEFAULT_KEYSTORE_FILENAME);

			if (!keystoreFile.exists()) {
				try {
					JettyKeystore.generateKeystoreAndSave(DEFAULT_KEYSTORE_DOMAINNAME, DEFAULT_KEYSTORE_ALIAS, DEFAULT_KEYSTORE_PASSWORD, keystoreFile);
				} catch (JettyKeystoreException e) {
					throw new JettyBootstrapException("Can't generate keyStore", e);
				}
			}
			iJettyConfiguration.setSslKeyStorePath(keystoreFile.getPath());
			iJettyConfiguration.setSslKeyStorePassword(DEFAULT_KEYSTORE_PASSWORD);
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

			SslContextFactory sslContextFactory = new SslContextFactory(iJettyConfiguration.getSslKeyStorePath());
			sslContextFactory.setKeyStorePassword(iJettyConfiguration.getSslKeyStorePassword());
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
				stopServer();
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
			if (jettyHandler instanceof AbstractWebAppJettyHandler) {
				AbstractWebAppJettyHandler abstractWebAppJettyHandler = (AbstractWebAppJettyHandler) jettyHandler;
				abstractWebAppJettyHandler.setTempDirectory(iJettyConfiguration.getTempDirectory());
				abstractWebAppJettyHandler.setPersistTempDirectory(iJettyConfiguration.isPersistAppTempDirectories());
				abstractWebAppJettyHandler.setRedirectOnHttpsConnector(iJettyConfiguration.isRedirectWebAppsOnHttpsConnector());
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

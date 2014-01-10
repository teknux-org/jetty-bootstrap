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
import org.genux.jettybootstrap.handler.AbstractAppJettyHandler;
import org.genux.jettybootstrap.handler.ExplodedWarAppJettyHandler;
import org.genux.jettybootstrap.handler.IJettyHandler;
import org.genux.jettybootstrap.handler.JettyHandler;
import org.genux.jettybootstrap.handler.StaticResourceAppJettyHandler;
import org.genux.jettybootstrap.handler.WarAppFromClasspathJettyHandler;
import org.genux.jettybootstrap.handler.WarAppJettyHandler;
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
	 * Quick Start : {@link #addSelf()} and
	 * {@link #startServer()}
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
	 * Add War Application on ContextPath
	 * {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param war
	 *            FilePath
	 * @return
	 */
	public JettyBootstrap addWarApp(String war) {
		return addWarApp(war, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add War Application
	 * 
	 * @param war
	 *            FilePath
	 * @param contextPath
	 * @return
	 */
	public JettyBootstrap addWarApp(String war, String contextPath) {
		WarAppJettyHandler warAppJettyHandler = new WarAppJettyHandler();
		warAppJettyHandler.setWar(war);
		warAppJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(warAppJettyHandler);

		return this;
	}

	/**
	 * Add War Application from Classpath on ContextPath
	 * {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param warFromClasspath
	 * @return
	 */
	public JettyBootstrap addWarAppFromClasspath(String warFromClasspath) {
		return addWarAppFromClasspath(warFromClasspath, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add War Application from Classpath
	 * 
	 * @param warFromClasspath
	 * @param contextPath
	 * @return
	 */
	public JettyBootstrap addWarAppFromClasspath(String warFromClasspath, String contextPath) {
		WarAppFromClasspathJettyHandler warAppFromClasspathJettyHandler = new WarAppFromClasspathJettyHandler();
		warAppFromClasspathJettyHandler.setWarFromClasspath(warFromClasspath);
		warAppFromClasspathJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(warAppFromClasspathJettyHandler);

		return this;
	}

	/**
	 * Add Static Resource on ContextPath
	 * {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param webAppBase
	 * @return
	 */
	public JettyBootstrap addStaticResource(String webAppBase) {
		return addStaticResource(webAppBase, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add Static Resource
	 * 
	 * @param webAppBase
	 * @param contextPath
	 * @return
	 */
	public JettyBootstrap addStaticResource(String webAppBase, String contextPath) {
		StaticResourceAppJettyHandler staticResourceAppJettyHandler = new StaticResourceAppJettyHandler();
		staticResourceAppJettyHandler.setWebAppBase(webAppBase);
		staticResourceAppJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(staticResourceAppJettyHandler);

		return this;
	}

	/**
	 * Add Static Resource from Classpath on ContextPath
	 * {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param webAppResourceBase
	 * @return
	 */
	public JettyBootstrap addStaticResourceFromClasspath(String webAppResourceBase) {
		return addStaticResourceFromClasspath(webAppResourceBase, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add Static Application from Classpath
	 * 
	 * @param webAppResourceBase
	 * @param contextPath
	 * @return
	 */
	public JettyBootstrap addStaticResourceFromClasspath(String webAppResourceBase, String contextPath) {
		StaticResourceAppJettyHandler staticResourceAppJettyHandler = new StaticResourceAppJettyHandler();
		staticResourceAppJettyHandler.setWebAppBaseFromClasspath(webAppResourceBase);
		staticResourceAppJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(staticResourceAppJettyHandler);

		return this;
	}

	/**
	 * Add Exploded War Application on ContextPath
	 * {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param webAppBase
	 * @param descriptor
	 * @return
	 */
	public JettyBootstrap addExplodedWarApp(String webAppBase, String descriptor) {
		return addExplodedWarApp(webAppBase, descriptor, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add Exploded War Application
	 * 
	 * @param webAppBase
	 * @param descriptor
	 * @param contextPath
	 * @return
	 */
	public JettyBootstrap addExplodedWarApp(String webAppBase, String descriptor, String contextPath) {
		ExplodedWarAppJettyHandler explodedWarAppJettyHandler = new ExplodedWarAppJettyHandler();
		explodedWarAppJettyHandler.setWebAppBase(webAppBase);
		explodedWarAppJettyHandler.setDescriptor(descriptor);
		explodedWarAppJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(explodedWarAppJettyHandler);

		return this;
	}

	/**
	 * Add Exploded War Application from Classpath on
	 * ContextPath {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param webAppResourceBase
	 * @param descriptor
	 * @return
	 */
	public JettyBootstrap addExplodedWarAppFromClasspath(String webAppResourceBase, String descriptor) {
		return addExplodedWarAppFromClasspath(webAppResourceBase, descriptor, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add Exploded War Application from Classpath
	 * 
	 * @param webAppResourceBase
	 * @param descriptor
	 * @param contextPath
	 * @return
	 */
	public JettyBootstrap addExplodedWarAppFromClasspath(String webAppResourceBase, String descriptor, String contextPath) {
		ExplodedWarAppJettyHandler explodedWarAppJettyHandler = new ExplodedWarAppJettyHandler();
		explodedWarAppJettyHandler.setWebAppBaseFromClasspath(webAppResourceBase);
		explodedWarAppJettyHandler.setDescriptor(descriptor);
		explodedWarAppJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(explodedWarAppJettyHandler);

		return this;
	}

	/**
	 * Add Application from Myself on ContextPath
	 * {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @see #addExplodedWarAppFromClasspath(String, String)
	 * @return
	 */
	public JettyBootstrap addSelf() {
		return addExplodedWarAppFromClasspath(RESOURCE_WEBAPP, null);
	}

	/**
	 * Add Application from Myself
	 * 
	 * @see #addExplodedWarAppFromClasspath(String, String,
	 *      String)
	 * @param contextPath
	 * @return
	 */
	public JettyBootstrap addSelf(String contextPath) {
		return addExplodedWarAppFromClasspath(RESOURCE_WEBAPP, null, contextPath);
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
			if (jettyHandler instanceof AbstractAppJettyHandler) {
				AbstractAppJettyHandler abstractAppJettyHandler = (AbstractAppJettyHandler) jettyHandler;
				abstractAppJettyHandler.setTempDirectory(iJettyConfiguration.getTempDirectory());
				abstractAppJettyHandler.setPersistTempDirectory(iJettyConfiguration.isPersistAppTempDirectories());
				abstractAppJettyHandler.setRedirectOnHttpsConnector(iJettyConfiguration.isRedirectWebAppsOnHttpsConnector());
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

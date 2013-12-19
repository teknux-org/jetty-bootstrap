package org.genux.jettybootstrap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
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


public class JettyBootstrap {

	private static final Logger LOGGER = Logger.getLogger(JettyBootstrap.class);

	private static final String KEYSTORE_FILENAME = "application.keystore";
	private static final String KEYSTORE_DOMAINNAME = "unknown";
	private static final String KEYSTORE_ALIAS = "jettybootstrap";
	private static final String KEYSTORE_PASSWORD = "jettybootstrap";

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
	 * @throws JettyException
	 */
	public JettyBootstrap startJetty() throws JettyException {
		try {
			return startJetty(iJettyConfiguration.isAutoJoinOnStart());
		} catch (Exception e) {
			throw new JettyException(e);
		}
	}

	/**
	 * Start Jetty
	 * 
	 * @param join
	 * @return
	 * @throws JettyException
	 */
	public JettyBootstrap startJetty(boolean join) throws JettyException {
		if (server == null) {
			init(iJettyConfiguration);
		}
		addHandlers();

		LOGGER.info("Start Jetty...");
		try {
			server.start();
		} catch (Exception e) {
			throw new JettyException(e);
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
	 * @throws JettyException
	 */
	public JettyBootstrap joinJetty() throws JettyException {
		LOGGER.debug("Join Jetty...");

		try {
			server.join();
		} catch (InterruptedException e) {
			throw new JettyException(e);
		}

		return this;
	}

	/**
	 * Stop Jetty
	 * 
	 * @return
	 * @throws JettyException
	 */
	public JettyBootstrap stopJetty() throws JettyException {
		try {
			handlers.stop();

			if (server.isStarted()) {
				LOGGER.info("Stop Jetty...");

				server.stop();
			} else {
				LOGGER.warn("Can't stop Jetty. Already stopped");
			}
		} catch (Exception e) {
			throw new JettyException(e);
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
	 * @throws JettyException
	 */
	public Server getServer() throws JettyException {
		if (server == null) {
			init(iJettyConfiguration);
		}
		return server;
	}

	protected void init(IJettyConfiguration iJettyConfiguration) throws JettyException {
		this.iJettyConfiguration = initConfiguration(iJettyConfiguration);

		server = createServer(iJettyConfiguration);
		server.setConnectors(createConnectors(iJettyConfiguration, server));

		server.setHandler(handlers);

		createShutdownHook(iJettyConfiguration);
	}

	protected IJettyConfiguration initConfiguration(IJettyConfiguration iJettyConfiguration) throws JettyException {
		LOGGER.debug("Init Configuration...");

		LOGGER.trace("Check Temp Directory...");
		if (iJettyConfiguration.getTempDirectory() == null) {
			iJettyConfiguration.setTempDirectory(TEMP_DIRECTORY_DEFAULT);
		}

		if (iJettyConfiguration.getTempDirectory().exists() && iJettyConfiguration.isCleanTempDir()) {
			LOGGER.trace("Clean Temp Directory...");

			try {
				FileUtils.deleteDirectory(iJettyConfiguration.getTempDirectory());
			} catch (IOException e) {
				throw new JettyException("Can't clean temporary directory");
			}
		}
		if (!iJettyConfiguration.getTempDirectory().exists() && !iJettyConfiguration.getTempDirectory().mkdirs()) {
			throw new JettyException("Can't create temporary directory");
		}

		LOGGER.trace("Check required properties...");
		if (iJettyConfiguration.getHost() == null || iJettyConfiguration.getHost().isEmpty()) {
			throw new JettyException("Host not specified");
		}

		LOGGER.trace("Check connectors...");
		if (iJettyConfiguration.hasJettyConnector(JettyConnector.HTTPS) && (iJettyConfiguration.getSSLKeyStorePath() == null || iJettyConfiguration.getSSLKeyStorePath().isEmpty())) {
			File keystoreFile = new File(iJettyConfiguration.getTempDirectory().getPath() + File.separator + KEYSTORE_FILENAME);

			if (!keystoreFile.exists()) {
				try {
					JettyKeystore.generateKeystoreAndSave(KEYSTORE_DOMAINNAME, KEYSTORE_ALIAS, KEYSTORE_PASSWORD, keystoreFile);
				} catch (JettyKeystoreException e) {
					throw new JettyException("Can't generate keyStore", e);
				}
			}
			iJettyConfiguration.setSSLKeyStorePath(keystoreFile.getPath());
			iJettyConfiguration.setSSLKeyStorePassword(KEYSTORE_PASSWORD);
		}

		if (iJettyConfiguration.isRedirectWebAppsOnHttpsConnector() &&
			(!iJettyConfiguration.hasJettyConnector(JettyConnector.HTTP) || !iJettyConfiguration.hasJettyConnector(JettyConnector.HTTPS))) {
			throw new JettyException("You can't redirect all from HTTP to HTTPS Connector if both connectors are not setted");
		}

		LOGGER.trace(iJettyConfiguration);

		return iJettyConfiguration;
	}

	protected Server createServer(IJettyConfiguration iJettyConfiguration) {
		LOGGER.trace("Create Jetty Server...");

		Server server = new Server(new QueuedThreadPool(iJettyConfiguration.getMaxThreads()));
		server.setStopAtShutdown(false); //Reimplemented. See @IJettyConfiguration.stopAtShutdown
		server.setStopTimeout(iJettyConfiguration.getStopTimeout());

		return server;
	}

	protected Connector[] createConnectors(IJettyConfiguration iJettyConfiguration, Server server) {
		LOGGER.trace("Create Jetty Connectors...");

		List<Connector> connectors = new ArrayList<Connector>();

		if (iJettyConfiguration.hasJettyConnector(JettyConnector.HTTP)) {
			LOGGER.trace("Add HTTP Connector...");

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
			LOGGER.trace("Add HTTPS Connector...");

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
			LOGGER.debug("Shutdown...");
			if (iJettyConfiguration.isStopAtShutdown()) {
				stopJetty();
			}
		} catch (Exception e) {
			LOGGER.error(e);
		}
	}

	/**
	 * Add Handlers to jetty
	 * 
	 * @throws JettyException
	 */
	private void addHandlers() throws JettyException {
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
		LOGGER.trace("Create Jetty ShutdownHook...");

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

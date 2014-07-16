/*******************************************************************************
 * (C) Copyright 2014 Teknux.org (http://teknux.org/).
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 * Contributors:
 *      "Pierre PINON"
 *      "Francois EYL"
 *      "Laurent MARCHAL"
 *  
 *******************************************************************************/
package org.teknux.jettybootstrap;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teknux.jettybootstrap.configuration.IJettyConfiguration;
import org.teknux.jettybootstrap.configuration.JettyConnector;
import org.teknux.jettybootstrap.configuration.PropertiesJettyConfiguration;
import org.teknux.jettybootstrap.handler.ExplodedWarAppJettyHandler;
import org.teknux.jettybootstrap.handler.HandlerBuilder;
import org.teknux.jettybootstrap.handler.IJettyHandler;
import org.teknux.jettybootstrap.handler.JettyHandler;
import org.teknux.jettybootstrap.handler.WarAppFromClasspathJettyHandler;
import org.teknux.jettybootstrap.handler.WarAppJettyHandler;
import org.teknux.jettybootstrap.handler.listener.JettyLifeCycleLogListener;
import org.teknux.jettybootstrap.keystore.JettyKeystore;
import org.teknux.jettybootstrap.keystore.JettyKeystoreException;


/**
 * Main class for easily boostrapping jetty.
 */
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

	private Server server = null;
	private HandlerList handlers = new HandlerList();

	/**
	 * Shortcut to start Jetty when called within a JAR file
	 * containing the WEB-INF folder and needed libraries.
	 * <p>
	 * Basically uses {@link #addSelf()} and
	 * {@link #startServer()}
	 * 
	 * @return a new instance of {@link JettyBootstrap}
	 * @throws JettyBootstrapException
	 *             if an error occurs during the startup
	 */
	public static JettyBootstrap startSelf() throws JettyBootstrapException {
	    JettyBootstrap jettyBootstrap = new JettyBootstrap();
	    jettyBootstrap.addSelf();
		return jettyBootstrap.startServer();
	}

	/**
	 * Default constructor using the default
	 * {@link PropertiesJettyConfiguration} configuration.
	 */
	public JettyBootstrap() {
		this(new PropertiesJettyConfiguration());
	}

	/**
	 * Constructor specifiying the configuration properties.
	 * 
	 * @param configuration
	 *            the {@link IJettyConfiguration}
	 *            implementation of the configuration
	 */
	public JettyBootstrap(IJettyConfiguration configuration) {
		this.iJettyConfiguration = configuration;
	}

	/**
	 * Starts the Jetty Server and join the calling thread
	 * according to
	 * {@link IJettyConfiguration#isAutoJoinOnStart()}
	 * 
	 * @return this instance
	 * @throws JettyBootstrapException
	 *             if an exception occurs during the
	 *             initialization
	 * @see #startServer(boolean)
	 */
	public JettyBootstrap startServer() throws JettyBootstrapException {
		return startServer(iJettyConfiguration.isAutoJoinOnStart());
	}

	/**
	 * Starts the Jetty Server and join the calling thread.
	 * 
	 * @param join
	 *            <code>true</code> to block the calling
	 *            thread until the server stops.
	 *            <code>false</code> otherwise
	 * @return this instance
	 * @throws JettyBootstrapException
	 *             if an exception occurs during the
	 *             initialization
	 */
	public JettyBootstrap startServer(boolean join) throws JettyBootstrapException {
		logger.info("Starting Server...");

		if (server == null) {
			init(iJettyConfiguration);
		}
		initHandlers();

		try {
			server.start();
		} catch (Exception e) {
			throw new JettyBootstrapException(e);
		}

		// display server addresses
		if (iJettyConfiguration.getJettyConnectors().contains(JettyConnector.HTTP)) {
		    logger.info("http://{}:{}", iJettyConfiguration.getHost(), iJettyConfiguration.getPort());
		}
		if (iJettyConfiguration.getJettyConnectors().contains(JettyConnector.HTTPS)) {
            logger.info("https://{}:{}", iJettyConfiguration.getHost(), iJettyConfiguration.getSslPort());
        }

		if (join) {
			joinServer();
		}

		return this;
	}

	/**
	 * Blocks the calling thread until the server stops.
	 * 
	 * @return this instance
	 * @throws JettyBootstrapException
	 *             if an exception occurs while blocking the
	 *             thread
	 */
	public JettyBootstrap joinServer() throws JettyBootstrapException {
		try {
			if (isServerStarted()) {
				logger.debug("Joining Server...");

				server.join();
			} else {
				logger.warn("Can't join Server. Not started");
			}
		} catch (InterruptedException e) {
			throw new JettyBootstrapException(e);
		}

		return this;
	}
	
	/**
	 * Return if server is started
	 * 
	 * @return if server is started
	 */
	public boolean isServerStarted() {
	    return (server != null && server.isStarted());
	}

	/**
	 * Stops the Jetty server.
	 * 
	 * @return this instance
	 * @throws JettyBootstrapException
	 *             if an exception occurs while stopping the
	 *             server or if the server is not started
	 */
	public JettyBootstrap stopServer() throws JettyBootstrapException {
		logger.info("Stopping Server...");
		try {
			handlers.stop();

			if (isServerStarted()) {
				server.stop();
				
				logger.info("Server stopped.");
			} else {
				logger.warn("Can't stop server. Already stopped");
			}
		} catch (Exception e) {
			throw new JettyBootstrapException(e);
		}

		return this;
	}

	/**
	 * Add a War application the default context path
	 * {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param war
	 *            the path to a war file
	 * @return Handler
	 * @throws JettyBootstrapException 
	 */
	public Handler addWarApp(String war) throws JettyBootstrapException {
		return addWarApp(war, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add a War application specifying the context path.
	 * 
	 * @param war
	 *            the path to a war file
	 * @param contextPath
	 *            the path (base URL) to make the war
	 *            available
	 * @return Handler
	 * @throws JettyBootstrapException 
	 */
	public Handler addWarApp(String war, String contextPath) throws JettyBootstrapException {
		WarAppJettyHandler warAppJettyHandler = new WarAppJettyHandler();
		warAppJettyHandler.setWar(war);
		warAppJettyHandler.setContextPath(contextPath);

		return addJettyHandler(warAppJettyHandler);
	}

	/**
	 * Add a War application from the current classpath on
	 * the default context path {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param warFromClasspath
	 *            the path to a war file in the classpath
	 * @return Handler
	 * @throws JettyBootstrapException 
	 */
	public Handler addWarAppFromClasspath(String warFromClasspath) throws JettyBootstrapException {
		return addWarAppFromClasspath(warFromClasspath, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add a War application from the current classpath
	 * specifying the context path.
	 * 
	 * @param warFromClasspath
	 *            the path to a war file in the classpath
	 * @param contextPath
	 *            the path (base URL) to make the war
	 *            available
	 * @return Handler
	 * @throws JettyBootstrapException 
	 */
	public Handler addWarAppFromClasspath(String warFromClasspath, String contextPath) throws JettyBootstrapException {
		WarAppFromClasspathJettyHandler warAppFromClasspathJettyHandler = new WarAppFromClasspathJettyHandler();
		warAppFromClasspathJettyHandler.setWarFromClasspath(warFromClasspath);
		warAppFromClasspathJettyHandler.setContextPath(contextPath);
        warAppFromClasspathJettyHandler.setTempDirectory(iJettyConfiguration.getTempDirectory());

		return addJettyHandler(warAppFromClasspathJettyHandler);
	}

	/**
	 * Add an exploded (not packaged) War application on the
	 * default context path {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param explodedWar
	 *            the exploded war path
	 * @param descriptor
	 *            the web.xml descriptor path
	 * @return Handler
	 * @throws JettyBootstrapException 
	 */
	public Handler addExplodedWarApp(String explodedWar, String descriptor) throws JettyBootstrapException {
		return addExplodedWarApp(explodedWar, descriptor, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add an exploded (not packaged) War application
	 * specifying the context path.
	 * 
	 * @param explodedWar
	 *            the exploded war path
	 * @param descriptor
	 *            the web.xml descriptor path
	 * @param contextPath
	 *            the path (base URL) to make the resource
	 *            available
	 * @return Handler
	 * @throws JettyBootstrapException 
	 */
	public Handler addExplodedWarApp(String explodedWar, String descriptor, String contextPath) throws JettyBootstrapException {
		ExplodedWarAppJettyHandler explodedWarAppJettyHandler = new ExplodedWarAppJettyHandler();
		explodedWarAppJettyHandler.setWebAppBase(explodedWar);
		explodedWarAppJettyHandler.setDescriptor(descriptor);
		explodedWarAppJettyHandler.setContextPath(contextPath);

		return addJettyHandler(explodedWarAppJettyHandler);
	}

	/**
	 * Add an exploded (not packaged) War application from
	 * the current classpath, on the default context path
	 * {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param explodedWar
	 *            the exploded war path
	 * @return Handler
	 * @throws JettyBootstrapException 
	 */
	public Handler addExplodedWarAppFromClasspath(String explodedWar) throws JettyBootstrapException {
		return addExplodedWarAppFromClasspath(explodedWar, null);
	}

	/**
	 * Add an exploded (not packaged) War application from
	 * the current classpath, on the default context path
	 * {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param explodedWar
	 *            the exploded war path
	 * @param descriptor
	 *            the web.xml descriptor path
	 * @return Handler
	 * @throws JettyBootstrapException 
	 */
	public Handler addExplodedWarAppFromClasspath(String explodedWar, String descriptor) throws JettyBootstrapException {
		return addExplodedWarAppFromClasspath(explodedWar, descriptor, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add an exploded (not packaged) War application from
	 * the current classpath, specifying the context path.
	 * 
	 * @param explodedWar
	 *            the exploded war path
	 * @param descriptor
	 *            the web.xml descriptor path
	 * @param contextPath
	 *            the path (base URL) to make the resource
	 *            available
	 * @return Handler
	 * @throws JettyBootstrapException 
	 */
	public Handler addExplodedWarAppFromClasspath(String explodedWar, String descriptor, String contextPath) throws JettyBootstrapException {
		ExplodedWarAppJettyHandler explodedWarAppJettyHandler = new ExplodedWarAppJettyHandler();
		explodedWarAppJettyHandler.setWebAppBaseFromClasspath(explodedWar);
		explodedWarAppJettyHandler.setDescriptor(descriptor);
		explodedWarAppJettyHandler.setContextPath(contextPath);

		return addJettyHandler(explodedWarAppJettyHandler);
	}

	/**
	 * Add an exploded War application found from
	 * {@value #RESOURCE_WEBAPP} in the current classpath on
	 * the default context path {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @see #addExplodedWarAppFromClasspath(String, String)
	 * @return Handler
	 * @throws JettyBootstrapException 
	 */
	public Handler addSelf() throws JettyBootstrapException {
		return addExplodedWarAppFromClasspath(RESOURCE_WEBAPP, null);
	}

	/**
	 * Add an exploded War application found from
	 * {@value #RESOURCE_WEBAPP} in the current classpath
	 * specifying the context path.
	 * 
	 * @see #addExplodedWarAppFromClasspath(String, String,
	 *      String)
	 * @param contextPath
	 *            the path (base URL) to make the resource
	 *            available
	 * @return Handler
	 * @throws JettyBootstrapException 
	 */
	public Handler addSelf(String contextPath) throws JettyBootstrapException {
		return addExplodedWarAppFromClasspath(RESOURCE_WEBAPP, null, contextPath);
	}

	/**
	 * Add Handler
	 * 
	 * @param handler
	 * @return Handler
	 * @throws JettyBootstrapException 
	 */
	public Handler addHandler(Handler handler) throws JettyBootstrapException {
		JettyHandler jettyHandler = new JettyHandler();
		jettyHandler.setHandler(handler);

        handler.addLifeCycleListener(JettyLifeCycleLogListener.getLogListener(handler));

		return addJettyHandler(jettyHandler);
	}

	/**
	 * Add Handler
	 * 
	 * @param iJettyHandler
	 * @return Handler
	 * @throws JettyBootstrapException 
	 */
	public Handler addJettyHandler(IJettyHandler iJettyHandler) throws JettyBootstrapException {
        if (server == null) {
            init(iJettyConfiguration);
        }
        
	    HandlerBuilder handlerBuilder = new HandlerBuilder(iJettyConfiguration, iJettyHandler);
	    Handler handler = handlerBuilder.build();
		handlers.addHandler(handler);

		return handler;
	}

	/**
	 * Get the jetty {@link Server} Object. Calls
	 * {@link #init(IJettyConfiguration)} if not initialized
	 * yet.
	 * 
	 * @return the contained jetty {@link Server} Object.
	 * @throws JettyBootstrapException
	 *             if an error occurs during
	 *             {@link #init(IJettyConfiguration)}
	 */
	public Server getServer() throws JettyBootstrapException {
		if (server == null) {
			init(iJettyConfiguration);
		}
		return server;
	}

	/**
	 * Initialize Jetty server using the given
	 * {@link IJettyConfiguration}. Basically creates the
	 * server, set connectors, handlers and adds the
	 * shutdown hook.
	 * 
	 * @param iJettyConfiguration
	 * @throws JettyBootstrapException
	 */
	protected void init(IJettyConfiguration iJettyConfiguration) throws JettyBootstrapException {
		this.iJettyConfiguration = initConfiguration(iJettyConfiguration);

		server = createServer(iJettyConfiguration);
		server.setConnectors(createConnectors(iJettyConfiguration, server));

		server.setHandler(handlers);

		createShutdownHook(iJettyConfiguration);
	}

	/**
	 * Parse the given {@link IJettyConfiguration} object,
	 * validate the configuration and initialize it. Clean
	 * temp directory if necessary and generates SSL
	 * keystore when necessary.
	 * 
	 * @param iJettyConfiguration
	 * @return
	 * @throws JettyBootstrapException
	 */
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

	/**
	 * Convenient method used to build and return a new
	 * {@link Server}.
	 * 
	 * @param iJettyConfiguration
	 * @return
	 */
	protected Server createServer(IJettyConfiguration iJettyConfiguration) {
		logger.trace("Create Jetty Server...");

		Server server = new Server(new QueuedThreadPool(iJettyConfiguration.getMaxThreads()));
		server.setStopAtShutdown(false); //Reimplemented. See @IJettyConfiguration.stopAtShutdown
		server.setStopTimeout(iJettyConfiguration.getStopTimeout());

		return server;
	}

	/**
	 * Creates and returns the necessary
	 * {@link ServerConnector} based on the given
	 * {@link IJettyConfiguration}.
	 * 
	 * @param iJettyConfiguration
	 * @param server
	 *            the server to
	 * @return
	 */
	protected Connector[] createConnectors(IJettyConfiguration iJettyConfiguration, Server server) {
		logger.trace("Creating Jetty Connectors...");

		List<Connector> connectors = new ArrayList<Connector>();

		if (iJettyConfiguration.hasJettyConnector(JettyConnector.HTTP)) {
			logger.trace("Adding HTTP Connector...");

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
			logger.trace("Adding HTTPS Connector...");

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

	/**
	 * Convenient method used to gracefully stop Jetty
	 * server. Invoked by the registered shutdown hook.
	 * 
	 * @param iJettyConfiguration
	 */
	protected void shutdown(IJettyConfiguration iJettyConfiguration) {
		try {
			if (iJettyConfiguration.isStopAtShutdown()) {
			    logger.debug("Shutting Down...");
				stopServer();
			}
		} catch (Exception e) {
			logger.error("Shutdown", e);
		}
	}

	/**
	 * Init Handlers
	 * 
	 * @throws JettyBootstrapException
	 */
	private void initHandlers() throws JettyBootstrapException {
		for (Handler handler : handlers.getHandlers()) {
			logger.debug("Deploying {}...", handler);
		}
	}

	/**
	 * Create Shutdown Hook.
	 * 
	 * @param configuration
	 */
	private void createShutdownHook(final IJettyConfiguration configuration) {
		logger.trace("Creating Jetty ShutdownHook...");

		Runtime.getRuntime().addShutdownHook(new Thread() {

			public void run() {
				shutdown(configuration);
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

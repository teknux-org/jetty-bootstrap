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
import org.teknux.jettybootstrap.handler.AbstractAppJettyHandler;
import org.teknux.jettybootstrap.handler.ExplodedWarAppJettyHandler;
import org.teknux.jettybootstrap.handler.IJettyHandler;
import org.teknux.jettybootstrap.handler.JettyHandler;
import org.teknux.jettybootstrap.handler.StaticResourceAppJettyHandler;
import org.teknux.jettybootstrap.handler.WarAppFromClasspathJettyHandler;
import org.teknux.jettybootstrap.handler.WarAppJettyHandler;
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

	private IJettyConfiguration jettyConfiguration;
	private List<IJettyHandler> jettyHandlers = new ArrayList<IJettyHandler>();

	private Server server = null;
	private HandlerList handlers = new HandlerList();

	/**
	 * Shortcut to start Jetty when called within a JAR file containing the WEB-INF folder and needed libraries.
	 * <p>
	 * Basically uses {@link #addSelf()} and {@link #startServer()}
	 * 
	 * @return a new instance of {@link JettyBootstrap}
	 * @throws JettyBootstrapException
	 *             if an error occurs during the startup
	 */
	public static JettyBootstrap startSelf() throws JettyBootstrapException {
		return new JettyBootstrap().addSelf().startServer();
	}

	/**
	 * Default constructor using the default {@link PropertiesJettyConfiguration} configuration.
	 */
	public JettyBootstrap() {
		this(new PropertiesJettyConfiguration());
	}

	/**
	 * Constructor specifiying the configuration properties.
	 * 
	 * @param configuration
	 *            the {@link IJettyConfiguration} implementation of the configuration
	 */
	public JettyBootstrap(IJettyConfiguration configuration) {
		this.jettyConfiguration = configuration;
	}

	/**
	 * Starts the Jetty Server and join the calling thread according to {@link IJettyConfiguration#isAutoJoinOnStart()}
	 * 
	 * @return this instance
	 * @throws JettyBootstrapException
	 *             if an exception occurs during the initialization
	 * @see #startServer(boolean)
	 */
	public JettyBootstrap startServer() throws JettyBootstrapException {
		return startServer(jettyConfiguration.isAutoJoinOnStart());
	}

	/**
	 * Starts the Jetty Server and join the calling thread.
	 * 
	 * @param join
	 *            <code>true</code> to block the calling thread until the server stops. <code>false</code> otherwise
	 * @return this instance
	 * @throws JettyBootstrapException
	 *             if an exception occurs during the initialization
	 */
	public JettyBootstrap startServer(boolean join) throws JettyBootstrapException {
		if (server == null) {
			init(jettyConfiguration);
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
	 * Blocks the calling thread until the server stops.
	 * 
	 * @return this instance
	 * @throws JettyBootstrapException
	 *             if an exception occurs while blocking the thread
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
	 * Stops the Jetty server.
	 * 
	 * @return this instance
	 * @throws JettyBootstrapException
	 *             if an exception occurs while stopping the server or if the server is not started
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
	 * Add a War application the default context path {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param war
	 *            the path to a war file
	 * @return this instance
	 */
	public JettyBootstrap addWarApp(String war) {
		return addWarApp(war, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add a War application specifying the context path.
	 * 
	 * @param war
	 *            the path to a war file
	 * @param contextPath
	 *            the path (base URL) to make the war available
	 * @return this instance
	 */
	public JettyBootstrap addWarApp(String war, String contextPath) {
		WarAppJettyHandler warAppJettyHandler = new WarAppJettyHandler();
		warAppJettyHandler.setWar(war);
		warAppJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(warAppJettyHandler);

		return this;
	}

	/**
	 * Add a War application from the current classpath on the default context path {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param warFromClasspath
	 *            the path to a war file in the classpath
	 * @return this instance
	 */
	public JettyBootstrap addWarAppFromClasspath(String warFromClasspath) {
		return addWarAppFromClasspath(warFromClasspath, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add a War application from the current classpath specifying the context path.
	 * 
	 * @param warFromClasspath
	 *            the path to a war file in the classpath
	 * @param contextPath
	 *            the path (base URL) to make the war available
	 * @return this instance
	 */
	public JettyBootstrap addWarAppFromClasspath(String warFromClasspath, String contextPath) {
		WarAppFromClasspathJettyHandler warAppFromClasspathJettyHandler = new WarAppFromClasspathJettyHandler();
		warAppFromClasspathJettyHandler.setWarFromClasspath(warFromClasspath);
		warAppFromClasspathJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(warAppFromClasspathJettyHandler);

		return this;
	}

	/**
	 * Add a static resource on the default context path {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param resource
	 *            the static resource (file or directory) to make available
	 * @return this instance
	 */
	public JettyBootstrap addStaticResource(String resource) {
		return addStaticResource(resource, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add a static resource specifying the context path.
	 * 
	 * @param resource
	 *            the static resource (file or directory) to make available
	 * @param contextPath
	 *            the path (base URL) to make the resource available
	 * @return this instance
	 */
	public JettyBootstrap addStaticResource(String resource, String contextPath) {
		StaticResourceAppJettyHandler staticResourceAppJettyHandler = new StaticResourceAppJettyHandler();
		staticResourceAppJettyHandler.setWebAppBase(resource);
		staticResourceAppJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(staticResourceAppJettyHandler);

		return this;
	}

	/**
	 * Make a static resource from the current classpath available on the default context path
	 * {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param resource
	 *            the static resource (file or directory) to make available
	 * @return this instance
	 */
	public JettyBootstrap addStaticResourceFromClasspath(String resource) {
		return addStaticResourceFromClasspath(resource, CONTEXT_PATH_ROOT);
	}

	/**
	 * Make a static resource from the current classpath available, specifying the context path.
	 * 
	 * @param resource
	 *            the static resource (file or directory) to make available
	 * @param contextPath
	 *            the path (base URL) to make the resource available
	 * @return this instance
	 */
	public JettyBootstrap addStaticResourceFromClasspath(String resource, String contextPath) {
		StaticResourceAppJettyHandler staticResourceAppJettyHandler = new StaticResourceAppJettyHandler();
		staticResourceAppJettyHandler.setWebAppBaseFromClasspath(resource);
		staticResourceAppJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(staticResourceAppJettyHandler);

		return this;
	}

	/**
	 * Add an exploded (not packaged) War application on the default context path {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param explodedWar
	 *            the exploded war path
	 * @param descriptor
	 *            the web.xml descriptor path
	 * @return this instance
	 */
	public JettyBootstrap addExplodedWarApp(String explodedWar, String descriptor) {
		return addExplodedWarApp(explodedWar, descriptor, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add an exploded (not packaged) War application specifying the context path.
	 * 
	 * @param explodedWar
	 *            the exploded war path
	 * @param descriptor
	 *            the web.xml descriptor path
	 * @param contextPath
	 *            the path (base URL) to make the resource available
	 * @return this instance
	 */
	public JettyBootstrap addExplodedWarApp(String explodedWar, String descriptor, String contextPath) {
		ExplodedWarAppJettyHandler explodedWarAppJettyHandler = new ExplodedWarAppJettyHandler();
		explodedWarAppJettyHandler.setWebAppBase(explodedWar);
		explodedWarAppJettyHandler.setDescriptor(descriptor);
		explodedWarAppJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(explodedWarAppJettyHandler);

		return this;
	}

	/**
	 * Add an exploded (not packaged) War application from the current classpath, on the default context path
	 * {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @param explodedWar
	 *            the exploded war path
	 * @param descriptor
	 *            the web.xml descriptor path
	 * @return this instance
	 */
	public JettyBootstrap addExplodedWarAppFromClasspath(String explodedWar, String descriptor) {
		return addExplodedWarAppFromClasspath(explodedWar, descriptor, CONTEXT_PATH_ROOT);
	}

	/**
	 * Add an exploded (not packaged) War application from the current classpath, specifying the context path.
	 * 
	 * @param explodedWar
	 *            the exploded war path
	 * @param descriptor
	 *            the web.xml descriptor path
	 * @param contextPath
	 *            the path (base URL) to make the resource available
	 * @return this instance
	 */
	public JettyBootstrap addExplodedWarAppFromClasspath(String explodedWar, String descriptor, String contextPath) {
		ExplodedWarAppJettyHandler explodedWarAppJettyHandler = new ExplodedWarAppJettyHandler();
		explodedWarAppJettyHandler.setWebAppBaseFromClasspath(explodedWar);
		explodedWarAppJettyHandler.setDescriptor(descriptor);
		explodedWarAppJettyHandler.setContextPath(contextPath);

		jettyHandlers.add(explodedWarAppJettyHandler);

		return this;
	}

	/**
	 * Add an exploded War application found from {@value #RESOURCE_WEBAPP} in the current classpath on the default
	 * context path {@value #CONTEXT_PATH_ROOT}
	 * 
	 * @see #addExplodedWarAppFromClasspath(String, String)
	 * @return this instance
	 */
	public JettyBootstrap addSelf() {
		return addExplodedWarAppFromClasspath(RESOURCE_WEBAPP, null);
	}

	/**
	 * Add an exploded War application found from {@value #RESOURCE_WEBAPP} in the current classpath specifying the
	 * context path.
	 * 
	 * @see #addExplodedWarAppFromClasspath(String, String, String)
	 * @param contextPath
	 *            the path (base URL) to make the resource available
	 * @return this instance
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
	 * Get the jetty {@link Server} Object. Calls {@link #init(IJettyConfiguration)} if not initialized yet.
	 * 
	 * @return the contained jetty {@link Server} Object.
	 * @throws JettyBootstrapException
	 *             if an error occurs during {@link #init(IJettyConfiguration)}
	 */
	public Server getServer() throws JettyBootstrapException {
		if (server == null) {
			init(jettyConfiguration);
		}
		return server;
	}

	protected void init(IJettyConfiguration iJettyConfiguration) throws JettyBootstrapException {
		this.jettyConfiguration = initConfiguration(iJettyConfiguration);

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
				abstractAppJettyHandler.setTempDirectory(jettyConfiguration.getTempDirectory());
				abstractAppJettyHandler.setPersistTempDirectory(jettyConfiguration.isPersistAppTempDirectories());
				abstractAppJettyHandler.setRedirectOnHttpsConnector(jettyConfiguration.isRedirectWebAppsOnHttpsConnector());
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

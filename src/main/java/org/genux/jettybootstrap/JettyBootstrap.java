package org.genux.jettybootstrap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.genux.jettybootstrap.configuration.IJettyConfiguration;


public class JettyBootstrap {

	private static final Logger LOGGER = Logger.getLogger(JettyBootstrap.class);

	private static final String APP_DIRECTORY_NAME = "deploy";
	private static final String APP_PREFIX_FILENAME = "deploy";
	private static final String RESOURCEWAR_DIRECTORY_NAME = "war";
	private static final String RESOURCEWAR_PREFIX_FILENAME = "war";

	private static final String TEMP_DIRECTORY_NAME = ".jettyTemp";
	public static final File TEMP_DIRECTORY_JARDIR = new File(getJarDir().getPath() + File.separator + TEMP_DIRECTORY_NAME);
	public static final File TEMP_DIRECTORY_SYSTEMP = new File(System.getProperty("java.io.tmpdir") + File.separator + TEMP_DIRECTORY_NAME);
	protected static final File TEMP_DIRECTORY_DEFAULT = TEMP_DIRECTORY_JARDIR;

	private static final String WEB_ROOT = "/";

	private IJettyConfiguration iJettyConfiguration;
	private Server server;
	private HandlerList handlerList = new HandlerList();

	public JettyBootstrap(IJettyConfiguration iJettyConfiguration) throws JettyException {
		init(iJettyConfiguration);
	}

	protected void init(IJettyConfiguration iJettyConfiguration) throws JettyException {
		this.iJettyConfiguration = initConfiguration(iJettyConfiguration);

		server = createServer(this.iJettyConfiguration);
		server.setConnectors(createConnectors(this.iJettyConfiguration, server));

		server.setHandler(handlerList);

		if (this.iJettyConfiguration.getTempDirectory().exists()) {
			throw new JettyException(MessageFormat.format("Temporary directory already exists. Delete directory [{0}] manually", this.iJettyConfiguration.getTempDirectory()));
		}

		if (!new File(this.iJettyConfiguration.getTempDirectory() + File.separator + APP_DIRECTORY_NAME).mkdirs()) {
			throw new JettyException("Can't create temporary directory");
		}

		createShutdownHook(this.iJettyConfiguration);
	}

	protected IJettyConfiguration initConfiguration(IJettyConfiguration iJettyConfiguration) throws JettyException {
		LOGGER.trace("Init Configuration...");

		if (iJettyConfiguration.getTempDirectory() == null) {
			iJettyConfiguration.setTempDirectory(TEMP_DIRECTORY_DEFAULT);
		}

		if (iJettyConfiguration.getHost() == null || iJettyConfiguration.getHost().isEmpty()) {
			throw new JettyException("Host not specified");
		}

		if (iJettyConfiguration.hasJettyConnector(JettyConnector.SSL) && (iJettyConfiguration.getSSLKeyStorePath() == null || iJettyConfiguration.getSSLKeyStorePath().isEmpty())) {
			throw new JettyException("SSLKeyStorePath not specified");
		}

		if (iJettyConfiguration.hasJettyConnector(JettyConnector.DEFAULT) && iJettyConfiguration.hasJettyConnector(JettyConnector.REDIRECT_DEFAULT_TO_SSL)) {
			throw new JettyException("You can't choose 'default' connector with 'redirect default to ssl' connector");
		}

		if (iJettyConfiguration.hasJettyConnector(JettyConnector.REDIRECT_DEFAULT_TO_SSL) && !iJettyConfiguration.hasJettyConnector(JettyConnector.SSL)) {
			throw new JettyException("You can't choose 'redirect default to ssl' connector without 'ssl' connector");
		}

		LOGGER.trace(iJettyConfiguration);

		return iJettyConfiguration;
	}

	protected Server createServer(IJettyConfiguration iJettyConfiguration) {
		LOGGER.trace("Create Jetty Server...");

		Server server = new Server(new QueuedThreadPool(iJettyConfiguration.getMaxThreads()));
		server.setStopAtShutdown(false); //Reimplemented
		server.setStopTimeout(iJettyConfiguration.getStopTimeout());

		return server;
	}

	protected Connector[] createConnectors(IJettyConfiguration iJettyConfiguration, Server server) {
		LOGGER.trace("Create Jetty Connectors...");

		List<Connector> serverConnectors = new ArrayList<Connector>();

		if (iJettyConfiguration.hasJettyConnector(JettyConnector.SSL)) {
			LOGGER.trace("Add SSL Connector...");

			SslContextFactory sslContextFactory = new SslContextFactory(iJettyConfiguration.getSSLKeyStorePath());
			sslContextFactory.setKeyStorePassword(iJettyConfiguration.getSSLKeyStorePassword());
			ServerConnector serverConnector = new ServerConnector(server, sslContextFactory);
			serverConnector.setPort(iJettyConfiguration.getSslPort());

			serverConnector.setIdleTimeout(iJettyConfiguration.getIdleTimeout());
			serverConnector.setHost(iJettyConfiguration.getHost());

			serverConnectors.add(serverConnector);
		}
		if (iJettyConfiguration.hasJettyConnector(JettyConnector.DEFAULT)) {
			LOGGER.trace("Add Default Connector...");

			ServerConnector serverConnector = new ServerConnector(server);
			serverConnector.setPort(iJettyConfiguration.getPort());

			serverConnector.setIdleTimeout(iJettyConfiguration.getIdleTimeout());
			serverConnector.setHost(iJettyConfiguration.getHost());

			serverConnectors.add(serverConnector);
		}
		if (iJettyConfiguration.hasJettyConnector(JettyConnector.REDIRECT_DEFAULT_TO_SSL)) {
			LOGGER.trace("Redirect Default Connector on SSL Connector...");

			HttpConfiguration httpConfiguration = new HttpConfiguration();
			httpConfiguration.setSecurePort(iJettyConfiguration.getSslPort());

			HttpConnectionFactory httpConnectionFactory = new HttpConnectionFactory(httpConfiguration);

			ServerConnector serverConnector = new ServerConnector(server, httpConnectionFactory);
			serverConnector.setHost(iJettyConfiguration.getHost());
			serverConnector.setPort(iJettyConfiguration.getPort());

			serverConnectors.add(serverConnector);
		}

		return serverConnectors.toArray(new Connector[serverConnectors.size()]);
	}

	private void createShutdownHook(final IJettyConfiguration iJettyConfiguration) {
		LOGGER.trace("Create Jetty ShutdownHook...");

		Runtime.getRuntime().addShutdownHook(new Thread() {

			public void run() {
				shutdownHook(iJettyConfiguration);
			}
		});
	}

	protected void shutdownHook(IJettyConfiguration iJettyConfiguration) {
		try {
			LOGGER.debug("ShutdownHook");
			if (iJettyConfiguration.isStopAtShutdown()) {
				stopJetty();
			}

			LOGGER.trace("Delete Temp Directory...");
			FileUtils.deleteDirectory(iJettyConfiguration.getTempDirectory());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deployResource(String resource) throws JettyException {
		deployResource(resource, WEB_ROOT);
	}

	public void deployResource(String resource, String contextPath) throws JettyException {
		File resourcewarDirectory = new File(iJettyConfiguration.getTempDirectory().getPath() + File.separator + RESOURCEWAR_DIRECTORY_NAME);

		LOGGER.trace(MessageFormat.format("Copy war resource [{0}] to directory : [{1}]...", resource, resourcewarDirectory));

		if (!resourcewarDirectory.mkdir()) {
			throw new JettyException("Can't create resource war directory");
		}

		File resourcewarFile = new File(resourcewarDirectory.getPath() + File.separator + RESOURCEWAR_PREFIX_FILENAME + handlerList.getBeans().size() + ".war");

		InputStream inputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			inputStream = JettyBootstrap.class.getResourceAsStream(resource);
			fileOutputStream = new FileOutputStream(resourcewarFile);
			IOUtils.copy(inputStream, fileOutputStream);
		} catch (FileNotFoundException e) {
			throw new JettyException(e);
		} catch (IOException e) {
			throw new JettyException(e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (IOException e) {
				LOGGER.error("Can't closed streams ond deployResource", e);
			}
		}

		deployWar(resourcewarFile, contextPath);
	}

	public void deployWar(File warFile) {
		deployWar(warFile, WEB_ROOT);
	}

	public void deployWar(File warFile, String contextPath) {
		File tempDirectory = new File(iJettyConfiguration.getTempDirectory().getPath() + File.separator + APP_DIRECTORY_NAME + File.separator + APP_PREFIX_FILENAME +
			handlerList.getBeans().size());

		LOGGER.debug(MessageFormat.format("Deploy war [{0}] on context path [{1}] (Temp Directory : [{2}])...", warFile, contextPath, tempDirectory));

		WebAppContext webAppContext = new WebAppContext(warFile.getPath(), contextPath);

		webAppContext.setTempDirectory(tempDirectory);
		webAppContext.setParentLoaderPriority(iJettyConfiguration.getParentLoaderPriority());

		handlerList.addHandler(webAppContext);
	}

	public void startJetty() throws JettyException {
		try {
			startJetty(iJettyConfiguration.isAutoJoinOnStart());
		} catch (Exception e) {
			throw new JettyException(e);
		}
	}

	public void startJetty(boolean join) throws JettyException {
		if (handlerList.getHandlers().length == 0) {
			throw new JettyException("No handlers. Can't start Jetty");
		}
		if (server.isStopped()) {
			LOGGER.info("Start Jetty...");
			try {
				server.start();
			} catch (Exception e) {
				throw new JettyException(e);
			}

			if (join) {
				joinJetty();
			}
		} else {
			LOGGER.debug("Can't start Jetty. Already started");
		}
	}

	public void joinJetty() throws JettyException {
		LOGGER.debug("Join Jetty...");

		try {
			server.join();
		} catch (InterruptedException e) {
			throw new JettyException(e);
		}
	}

	public void stopJetty() throws JettyException {
		try {
			handlerList.stop();

			if (server.isStarted()) {
				LOGGER.info("Stop Jetty...");

				server.stop();
			} else {
				LOGGER.warn("Can't stop Jetty. Already stopped");
			}
		} catch (Exception e) {
			throw new JettyException(e);
		}
	}

	private static File getJarDir() {
		return new File(JettyBootstrap.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile();
	}

	public Server getServer() {
		return server;
	}
}

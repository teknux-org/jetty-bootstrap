package org.teknux.jettybootstrap.test.jettybootstrap;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.After;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;
import org.teknux.jettybootstrap.JettyBootstrap;
import org.teknux.jettybootstrap.JettyBootstrapException;
import org.teknux.jettybootstrap.configuration.JettyConfiguration;
import org.teknux.jettybootstrap.configuration.JettyConnector;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JettyBootstrapTest {

	private final static String HOST = "127.0.0.1";
	private final static int PORT = 0;
	private final static int SSL_PORT = 0;
	private final static int SOCKET_TIMEOUT = 1000;

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Boolean ssl = null;
	private JettyBootstrap jettyBootstrap = null;

	public JettyBootstrap initServer(boolean ssl) throws JettyBootstrapException, IOException {
		JettyConfiguration jettyConfiguration = new JettyConfiguration();
		jettyConfiguration.setStopAtShutdown(false);
		jettyConfiguration.setAutoJoinOnStart(false);
		jettyConfiguration.setTempDirectory(temporaryFolder.newFolder());
		jettyConfiguration.setCleanTempDir(true);
		jettyConfiguration.setPersistAppTempDirectories(false);
		jettyConfiguration.setHost(HOST);
		if (ssl) {
			jettyConfiguration.setJettyConnectors(JettyConnector.HTTPS);
			jettyConfiguration.setSslPort(SSL_PORT);
		} else {
			jettyConfiguration.setJettyConnectors(JettyConnector.HTTP);
			jettyConfiguration.setPort(PORT);
		}

		this.ssl = ssl;
		this.jettyBootstrap = new JettyBootstrap(jettyConfiguration);
		return this.jettyBootstrap;
	}

	@After
	public void stopServer() throws JettyBootstrapException {
		jettyBootstrap.stopServer();
	}

	private int getPort() throws JettyBootstrapException {
		//It's random port (0). Return real port
		return ((ServerConnector) jettyBootstrap.getServer().getConnectors()[0]).getLocalPort();
	}

	private SimpleResponse get(String url) throws IllegalStateException, IOException, JettyBootstrapException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		SimpleResponse simpleResponse = new SimpleResponse();

		CloseableHttpClient httpClient = null;
		HttpGet httpGet = null;
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).build();

		if (ssl) {
			SSLContextBuilder sSLContextBuilder = new SSLContextBuilder();
			sSLContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
			SSLConnectionSocketFactory sSLConnectionSocketFactory = new SSLConnectionSocketFactory(sSLContextBuilder.build(),
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			httpClient = HttpClients.custom().setSSLSocketFactory(sSLConnectionSocketFactory).build();

			httpGet = new HttpGet("https://" + HOST + ":" + getPort() + url);
		} else {
			httpClient = HttpClients.createDefault();

			httpGet = new HttpGet("http://" + HOST + ":" + getPort() + url);
		}

		httpGet.setConfig(requestConfig);
		CloseableHttpResponse response = null;

		try {
			response = httpClient.execute(httpGet);
			simpleResponse.setStatusCode(response.getStatusLine().getStatusCode());
			simpleResponse.setContent(IOUtils.toString(response.getEntity().getContent()));
		} finally {
			if (response != null) {
				response.close();
			}
			httpClient.close();
		}

		return simpleResponse;
	}

	@Test
	public void do01StaticWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File file = temporaryFolder.newFile();
		copyResourceToFile("/static.war", file);

		initServer(false).addWarApp(file.getPath(), "/staticWar").startServer();

		Assert.assertEquals(new SimpleResponse(200, "test1content\n"), get("/staticWar/test1.html"));
		Assert.assertEquals(new SimpleResponse(200, "test2content\n"), get("/staticWar/test2.html"));
		Assert.assertEquals(new Integer(404), get("/test3.html").getStatusCode());
	}

	@Test
	public void do02SslStaticWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File file = temporaryFolder.newFile();
		copyResourceToFile("/static.war", file);

		initServer(true).addWarApp(file.getPath(), "/sslStaticWar").startServer();

		Assert.assertEquals(new SimpleResponse(200, "test1content\n"), get("/sslStaticWar/test1.html"));
		Assert.assertEquals(new SimpleResponse(200, "test2content\n"), get("/sslStaticWar/test2.html"));
		Assert.assertEquals(new Integer(404), get("/test3.html").getStatusCode());
	}

	@Test
	public void do03ServletWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File file = temporaryFolder.newFile();
		copyResourceToFile("/servlet.war", file);

		initServer(false).addWarApp(file.getPath(), "/servletWar").startServer();

		Assert.assertEquals(new SimpleResponse(200, "Value=value1\n"), get("/servletWar?value=value1"));
		Assert.assertEquals(new SimpleResponse(200, "Value=value2\n"), get("/servletWar?value=value2"));
	}

	@Test
	public void do04SslServletWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File file = temporaryFolder.newFile();
		copyResourceToFile("/servlet.war", file);

		initServer(true).addWarApp(file.getPath(), "/sslServletWar").startServer();

		Assert.assertEquals(new SimpleResponse(200, "Value=value1\n"), get("/sslServletWar?value=value1"));
		Assert.assertEquals(new SimpleResponse(200, "Value=value2\n"), get("/sslServletWar?value=value2"));
	}

	@Test
	public void do05StaticWarFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
		initServer(false).addWarAppFromClasspath("/static.war", "/staticWarFromClasspath").startServer();

		Assert.assertEquals(new SimpleResponse(200, "test1content\n"), get("/staticWarFromClasspath/test1.html"));
		Assert.assertEquals(new SimpleResponse(200, "test2content\n"), get("/staticWarFromClasspath/test2.html"));
		Assert.assertEquals(new Integer(404), get("/test3.html").getStatusCode());
	}

	@Test
	public void do06SslStaticWarFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
		initServer(true).addWarAppFromClasspath("/static.war", "/sslStaticWarFromClasspath").startServer();

		Assert.assertEquals(new SimpleResponse(200, "test1content\n"), get("/sslStaticWarFromClasspath/test1.html"));
		Assert.assertEquals(new SimpleResponse(200, "test2content\n"), get("/sslStaticWarFromClasspath/test2.html"));
		Assert.assertEquals(new Integer(404), get("/test3.html").getStatusCode());
	}

	@Test
	public void do07ServletWarFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
		initServer(false).addWarAppFromClasspath("/servlet.war", "/servletWarFromClasspath").startServer();

		Assert.assertEquals(new SimpleResponse(200, "Value=value1\n"), get("/servletWarFromClasspath?value=value1"));
		Assert.assertEquals(new SimpleResponse(200, "Value=value2\n"), get("/servletWarFromClasspath?value=value2"));
	}

	@Test
	public void do08SslServletWarFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
		initServer(true).addWarAppFromClasspath("/servlet.war", "/sslServletWarFromClasspath").startServer();

		Assert.assertEquals(new SimpleResponse(200, "Value=value1\n"), get("/sslServletWarFromClasspath?value=value1"));
		Assert.assertEquals(new SimpleResponse(200, "Value=value2\n"), get("/sslServletWarFromClasspath?value=value2"));
	}

	@Test
	public void do09StaticResourceTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File folder = temporaryFolder.newFolder();
		copyResourceToFile("/webapp", folder);

		initServer(false).addStaticResource(folder.getPath(), "/staticResource").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/staticResource/index.html"));
	}

	@Test
	public void do10SslStaticResourceTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, URISyntaxException {
		File folder = temporaryFolder.newFolder();
		copyResourceToFile("/webapp", folder);

		initServer(true).addStaticResource(folder.getPath(), "/sslStaticResource").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/sslStaticResource/index.html"));
	}

	@Test
	public void do11StaticResourceFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
		initServer(false).addStaticResourceFromClasspath("/webapp", "/staticResourceFromClasspath").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/staticResourceFromClasspath/index.html"));
	}

	@Test
	public void do12SslStaticResourceFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
		initServer(true).addStaticResourceFromClasspath("/webapp", "/sslStaticResourceFromClasspath").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/sslStaticResourceFromClasspath/index.html"));
	}

	@Test
	public void do13ExplodedWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File folder = temporaryFolder.newFolder();
		copyResourceToFile("/webapp", folder);

		initServer(false).addExplodedWarApp(folder.getPath(), null, "/explodedWar").startServer();

		System.out.println(getPort());
		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/explodedWar/index.html"));
	}

	@Test
	public void do14SslExplodedWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File folder = temporaryFolder.newFolder();
		copyResourceToFile("/webapp", folder);

		initServer(true).addExplodedWarApp(folder.getPath(), null, "/sslExplodedWar").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/sslExplodedWar/index.html"));
	}

	@Test
	public void do15ExplodedWarFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, URISyntaxException {
		initServer(false).addExplodedWarAppFromClasspath("/webapp", null, "/explodedWarFromClasspath").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/explodedWarFromClasspath/index.html"));
	}

	@Test
	public void do16SslExplodedWarFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, URISyntaxException {
		initServer(true).addExplodedWarAppFromClasspath("/webapp", null, "/sslExplodedWarFromClasspath").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/sslExplodedWarFromClasspath/index.html"));
	}

	@Test
	public void do17Self() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		initServer(false).addSelf().startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/index.html"));
	}

	@Test
	public void do18SslSelf() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		initServer(true).addSelf().startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/index.html"));
	}

	@Test
	public void do19HandlerTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/handler");
		context.addServlet(new ServletHolder(new TestServlet()), "/*");

		initServer(false).addHandler(context).startServer();

		Assert.assertEquals(new SimpleResponse(200, "ServletTestContent\n"), get("/handler"));
	}

	@Test
	public void do20SslHandlerTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/sslHandler");
		context.addServlet(new ServletHolder(new TestServlet()), "/*");

		initServer(true).addHandler(context).startServer();

		Assert.assertEquals(new SimpleResponse(200, "ServletTestContent\n"), get("/sslHandler"));
	}

	private File copyResourceToFile(String resource, File file) throws IOException, URISyntaxException {
		File fileSrc = new File(getClass().getResource(resource).toURI());

		if (fileSrc.isDirectory()) {
			FileUtils.copyDirectory(fileSrc, file);
		} else {
			FileUtils.copyFile(fileSrc, file);
		}

		return file;
	}
}

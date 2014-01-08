package org.genux.jettybootstrap.test.jettybootstrap;

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
import org.genux.jettybootstrap.JettyBootstrap;
import org.genux.jettybootstrap.JettyBootstrapException;
import org.genux.jettybootstrap.configuration.JettyConfiguration;
import org.genux.jettybootstrap.configuration.JettyConnector;
import org.junit.After;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;


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

		initServer(false).addWar(file.getPath(), "/staticWar").startServer();

		Assert.assertEquals(new SimpleResponse(200, "test1content\n"), get("/staticWar/test1.html"));
		Assert.assertEquals(new SimpleResponse(200, "test2content\n"), get("/staticWar/test2.html"));
		Assert.assertEquals(new Integer(404), get("/test3.html").getStatusCode());
	}

	@Test
	public void do02SslStaticWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File file = temporaryFolder.newFile();
		copyResourceToFile("/static.war", file);

		initServer(true).addWar(file.getPath(), "/sslStaticWar").startServer();

		Assert.assertEquals(new SimpleResponse(200, "test1content\n"), get("/sslStaticWar/test1.html"));
		Assert.assertEquals(new SimpleResponse(200, "test2content\n"), get("/sslStaticWar/test2.html"));
		Assert.assertEquals(new Integer(404), get("/test3.html").getStatusCode());
	}

	@Test
	public void do03ServletWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File file = temporaryFolder.newFile();
		copyResourceToFile("/servlet.war", file);

		initServer(false).addWar(file.getPath(), "/servletWar").startServer();

		Assert.assertEquals(new SimpleResponse(200, "Value=value1\n"), get("/servletWar?value=value1"));
		Assert.assertEquals(new SimpleResponse(200, "Value=value2\n"), get("/servletWar?value=value2"));
	}

	@Test
	public void do04SslServletWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File file = temporaryFolder.newFile();
		copyResourceToFile("/servlet.war", file);

		initServer(true).addWar(file.getPath(), "/sslServletWar").startServer();

		Assert.assertEquals(new SimpleResponse(200, "Value=value1\n"), get("/sslServletWar?value=value1"));
		Assert.assertEquals(new SimpleResponse(200, "Value=value2\n"), get("/sslServletWar?value=value2"));
	}

	@Test
	public void do05StaticResourceWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		initServer(false).addResourceWar("/static.war", "/staticResourceWar").startServer();

		Assert.assertEquals(new SimpleResponse(200, "test1content\n"), get("/staticResourceWar/test1.html"));
		Assert.assertEquals(new SimpleResponse(200, "test2content\n"), get("/staticResourceWar/test2.html"));
		Assert.assertEquals(new Integer(404), get("/test3.html").getStatusCode());
	}

	@Test
	public void do06SslStaticResourceWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
		initServer(true).addResourceWar("/static.war", "/sslStaticResourceWar").startServer();

		Assert.assertEquals(new SimpleResponse(200, "test1content\n"), get("/sslStaticResourceWar/test1.html"));
		Assert.assertEquals(new SimpleResponse(200, "test2content\n"), get("/sslStaticResourceWar/test2.html"));
		Assert.assertEquals(new Integer(404), get("/test3.html").getStatusCode());
	}

	@Test
	public void do07ServletResourceWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
		initServer(false).addResourceWar("/servlet.war", "/servletResourceWar").startServer();

		Assert.assertEquals(new SimpleResponse(200, "Value=value1\n"), get("/servletResourceWar?value=value1"));
		Assert.assertEquals(new SimpleResponse(200, "Value=value2\n"), get("/servletResourceWar?value=value2"));
	}

	@Test
	public void do08SslServletResourceWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
		initServer(true).addResourceWar("/servlet.war", "/sslServletResourceWar").startServer();

		Assert.assertEquals(new SimpleResponse(200, "Value=value1\n"), get("/sslServletResourceWar?value=value1"));
		Assert.assertEquals(new SimpleResponse(200, "Value=value2\n"), get("/sslServletResourceWar?value=value2"));
	}

	@Test
	public void do09StaticContentTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File folder = temporaryFolder.newFolder();
		copyResourceToFile("/webapp", folder);

		initServer(false).addStaticContent(folder.getPath(), "/staticContent").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/staticContent/index.html"));
	}

	@Test
	public void do10SslStaticContentTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File folder = temporaryFolder.newFolder();
		copyResourceToFile("/webapp", folder);

		initServer(true).addStaticContent(folder.getPath(), "/sslStaticContent").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/sslStaticContent/index.html"));
	}

	@Test
	public void do11StaticContentResourceTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
		initServer(false).addResourceStaticContent("/webapp", "/staticContentResource").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/staticContentResource/index.html"));
	}

	@Test
	public void do12SslStaticContentResourceTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
		initServer(true).addResourceStaticContent("/webapp", "/sslStaticContentResource").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/sslStaticContentResource/index.html"));
	}

	@Test
	public void do13Webapp() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File folder = temporaryFolder.newFolder();
		copyResourceToFile("/webapp", folder);

		initServer(false).add(folder.getPath(), null, "/webapp").startServer();

		System.out.println(getPort());
		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/webapp/index.html"));
	}

	@Test
	public void do14SslWebApp() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File folder = temporaryFolder.newFolder();
		copyResourceToFile("/webapp", folder);

		initServer(true).add(folder.getPath(), null, "/sslWebapp").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/sslWebapp/index.html"));
	}

	@Test
	public void do15WebappResource() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		initServer(false).addResource("/webapp", null, "/webappResource").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/webappResource/index.html"));
	}

	@Test
	public void do16SslWebAppResource() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		initServer(true).addResource("/webapp", null, "/sslWebappResource").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/sslWebappResource/index.html"));
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

package org.genux.jettybootstrap.test.jettybootstrap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JettyBootstrapTest {

	private final static String HOST = "127.0.0.1";
	private final static int PORT = 0;

	private static JettyBootstrap jettyBootstrap = null;

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Before
	public void initServer() throws IOException, JettyBootstrapException {
		JettyConfiguration jettyConfiguration = new JettyConfiguration();
		jettyConfiguration.setStopAtShutdown(false);
		jettyConfiguration.setAutoJoinOnStart(false);
		jettyConfiguration.setTempDirectory(temporaryFolder.newFolder());
		jettyConfiguration.setCleanTempDir(true);
		jettyConfiguration.setPersistAppTempDirectories(false);
		jettyConfiguration.setJettyConnectors(JettyConnector.HTTP);
		jettyConfiguration.setHost(HOST);
		jettyConfiguration.setPort(PORT);

		jettyBootstrap = new JettyBootstrap(jettyConfiguration);
	}

	@After
	public void stopServer() throws JettyBootstrapException {
		jettyBootstrap.stopJetty();
	}

	private int getPort() throws JettyBootstrapException {
		//It's random port (0). Return real port
		return ((ServerConnector) jettyBootstrap.getServer().getConnectors()[0]).getLocalPort();
	}

	private SimpleResponse get(String url) throws IllegalStateException, IOException, JettyBootstrapException {
		SimpleResponse simpleResponse = new SimpleResponse();

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet("http://" + HOST + ":" + getPort() + url);
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
	public void do01StaticResourceWarTest() throws IllegalStateException, IOException, JettyBootstrapException {
		jettyBootstrap.addResourceWar("/static.war", "/staticWar").startJetty();

		Assert.assertEquals(new SimpleResponse(200, "test1content\n"), get("/staticWar/test1.html"));
		Assert.assertEquals(new SimpleResponse(200, "test2content\n"), get("/staticWar/test2.html"));
		Assert.assertEquals(new Integer(404), get("/test3.html").getStatusCode());
	}

	@Test
	public void do02ServletResourceWarTest() throws IllegalStateException, IOException, JettyBootstrapException {
		jettyBootstrap.addResourceWar("/servlet.war", "/servletWar").startJetty();

		Assert.assertEquals(new SimpleResponse(200, "Value=value1\n"), get("/servletWar?value=value1"));
		Assert.assertEquals(new SimpleResponse(200, "Value=value2\n"), get("/servletWar?value=value2"));
	}

	@Test
	public void do03StaticResourceTest() throws IllegalStateException, IOException, JettyBootstrapException {
		jettyBootstrap.addResourceStaticContent("/staticres", "/staticresres").startJetty();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/staticresres/index.html"));
	}

	@Test
	public void do04StaticTest() throws IllegalStateException, IOException, JettyBootstrapException {
		File file = temporaryFolder.newFolder();
		copyResourceToFile("/staticres/index.html", new File(file.getPath() + File.separator + "index.html"));

		jettyBootstrap.addStaticContent(file, "/staticres").startJetty();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/staticres/index.html"));
	}

	@Test
	public void do06HandlerTest() throws IllegalStateException, IOException, JettyBootstrapException {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/servlet");
		context.addServlet(new ServletHolder(new TestServlet()), "/*");

		jettyBootstrap.addHandler(context).startJetty();

		Assert.assertEquals(new SimpleResponse(200, "ServletTestContent\n"), get("/servlet"));
	}

	private File copyResourceToFile(String resource, File file) throws IOException {
		InputStream inputStream = null;
		FileOutputStream fileOutputStream = null;
		try {
			inputStream = JettyBootstrapTest.class.getResourceAsStream(resource);
			fileOutputStream = new FileOutputStream(file);
			IOUtils.copy(inputStream, fileOutputStream);

			return file;
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		}
	}
}

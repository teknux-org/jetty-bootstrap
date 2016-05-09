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
package org.teknux.jettybootstrap.test.jettybootstrap;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.teknux.jettybootstrap.JettyBootstrap;
import org.teknux.jettybootstrap.JettyBootstrapException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JettyBootstrapTest extends AbstractJettyBootstrapTest {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	@Test
	public void do01StaticWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File file = temporaryFolder.newFile();
		copyResourceToFile("/static.war", file);

		JettyBootstrap jettyBootstrap = initServer(false);
		jettyBootstrap.addWarApp(file.getPath(), "/staticWar");
		jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "test1content\n"), get("/staticWar/test1.html"));
		Assert.assertEquals(new SimpleResponse(200, "test2content\n"), get("/staticWar/test2.html"));
		Assert.assertEquals(new Integer(404), get("/test3.html").getStatusCode());
	}

	@Test
	public void do02SslStaticWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File file = temporaryFolder.newFile();
		copyResourceToFile("/static.war", file);

		JettyBootstrap jettyBootstrap = initServer(true);
		jettyBootstrap.addWarApp(file.getPath(), "/sslStaticWar");
		jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "test1content\n"), get("/sslStaticWar/test1.html"));
		Assert.assertEquals(new SimpleResponse(200, "test2content\n"), get("/sslStaticWar/test2.html"));
		Assert.assertEquals(new Integer(404), get("/test3.html").getStatusCode());
	}

	@Test
	public void do03ServletWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File file = temporaryFolder.newFile();
		copyResourceToFile("/servlet.war", file);

		JettyBootstrap jettyBootstrap = initServer(false);
		jettyBootstrap.addWarApp(file.getPath(), "/servletWar");
		jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "Value=value1" + LINE_SEPARATOR), get("/servletWar?value=value1"));
		Assert.assertEquals(new SimpleResponse(200, "Value=value2" + LINE_SEPARATOR), get("/servletWar?value=value2"));
	}

	@Test
	public void do04SslServletWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File file = temporaryFolder.newFile();
		copyResourceToFile("/servlet.war", file);

		JettyBootstrap jettyBootstrap = initServer(true);
		jettyBootstrap.addWarApp(file.getPath(), "/sslServletWar");
		jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "Value=value1" + LINE_SEPARATOR), get("/sslServletWar?value=value1"));
		Assert.assertEquals(new SimpleResponse(200, "Value=value2" + LINE_SEPARATOR), get("/sslServletWar?value=value2"));
	}

	@Test
	public void do05StaticWarFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
	    JettyBootstrap jettyBootstrap = initServer(false);
	    jettyBootstrap.addWarAppFromClasspath("/static.war", "/staticWarFromClasspath");
	    jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "test1content\n"), get("/staticWarFromClasspath/test1.html"));
		Assert.assertEquals(new SimpleResponse(200, "test2content\n"), get("/staticWarFromClasspath/test2.html"));
		Assert.assertEquals(new Integer(404), get("/test3.html").getStatusCode());
	}

	@Test
	public void do06SslStaticWarFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
	    JettyBootstrap jettyBootstrap = initServer(true);
	    jettyBootstrap.addWarAppFromClasspath("/static.war", "/sslStaticWarFromClasspath");
	    jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "test1content\n"), get("/sslStaticWarFromClasspath/test1.html"));
		Assert.assertEquals(new SimpleResponse(200, "test2content\n"), get("/sslStaticWarFromClasspath/test2.html"));
		Assert.assertEquals(new Integer(404), get("/test3.html").getStatusCode());
	}

	@Test
	public void do07ServletWarFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
	    JettyBootstrap jettyBootstrap = initServer(false);
	    jettyBootstrap.addWarAppFromClasspath("/servlet.war", "/servletWarFromClasspath");
	    jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "Value=value1" + LINE_SEPARATOR), get("/servletWarFromClasspath?value=value1"));
		Assert.assertEquals(new SimpleResponse(200, "Value=value2" + LINE_SEPARATOR), get("/servletWarFromClasspath?value=value2"));
	}

	@Test
	public void do08SslServletWarFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
	    JettyBootstrap jettyBootstrap = initServer(true);
	    jettyBootstrap.addWarAppFromClasspath("/servlet.war", "/sslServletWarFromClasspath");
	    jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "Value=value1" + LINE_SEPARATOR), get("/sslServletWarFromClasspath?value=value1"));
		Assert.assertEquals(new SimpleResponse(200, "Value=value2" + LINE_SEPARATOR), get("/sslServletWarFromClasspath?value=value2"));
	}

	@Test
	public void do09StaticResourceTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File folder = temporaryFolder.newFolder();
		copyResourceToFile("/webapp", folder);

		JettyBootstrap jettyBootstrap = initServer(false);
		jettyBootstrap.addExplodedWarApp(folder.getPath(), null, "/staticResource");
		jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/staticResource/index.html"));
	}

	@Test
	public void do10SslStaticResourceTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, URISyntaxException {
		File folder = temporaryFolder.newFolder();
		copyResourceToFile("/webapp", folder);

		JettyBootstrap jettyBootstrap = initServer(true);
		jettyBootstrap.addExplodedWarApp(folder.getPath(), null, "/sslStaticResource");
		jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/sslStaticResource/index.html"));
	}

	@Test
	public void do11StaticResourceFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
	    JettyBootstrap jettyBootstrap = initServer(false);
	    jettyBootstrap.addExplodedWarAppFromClasspath("/webapp", null, "/staticResourceFromClasspath");
	    jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/staticResourceFromClasspath/index.html"));
	}

	@Test
	public void do12SslStaticResourceFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
	    JettyBootstrap jettyBootstrap = initServer(true);
	    jettyBootstrap.addExplodedWarAppFromClasspath("/webapp", null, "/sslStaticResourceFromClasspath");
	    jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/sslStaticResourceFromClasspath/index.html"));
	}

	@Test
	public void do13ExplodedWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File folder = temporaryFolder.newFolder();
		copyResourceToFile("/webapp", folder);

		JettyBootstrap jettyBootstrap = initServer(false);
		jettyBootstrap.addExplodedWarApp(folder.getPath(), null, "/explodedWar");
		jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/explodedWar/index.html"));
	}

	@Test
	public void do14SslExplodedWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File folder = temporaryFolder.newFolder();
		copyResourceToFile("/webapp", folder);

		JettyBootstrap jettyBootstrap = initServer(true);
		jettyBootstrap.addExplodedWarApp(folder.getPath(), null, "/sslExplodedWar");
		jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/sslExplodedWar/index.html"));
	}

	@Test
	public void do15ExplodedWarFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, URISyntaxException {
	    JettyBootstrap jettyBootstrap = initServer(false);
	    jettyBootstrap.addExplodedWarAppFromClasspath("/webapp", null, "/explodedWarFromClasspath");
	    jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/explodedWarFromClasspath/index.html"));
	}

	@Test
	public void do16SslExplodedWarFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, URISyntaxException {
	    JettyBootstrap jettyBootstrap = initServer(true);
	    jettyBootstrap.addExplodedWarAppFromClasspath("/webapp", null, "/sslExplodedWarFromClasspath");
	    jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/sslExplodedWarFromClasspath/index.html"));
	}

	@Test
	public void do17Self() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
	    JettyBootstrap jettyBootstrap = initServer(false);
	    jettyBootstrap.addSelf();
	    jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/index.html"));
	}

	@Test
	public void do18SslSelf() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
	    JettyBootstrap jettyBootstrap = initServer(true);
	    jettyBootstrap.addSelf();
	    jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/index.html"));
	}

	@Test
	public void do19HandlerTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/handler");
		context.addServlet(new ServletHolder(new TestServlet()), "/*");

		JettyBootstrap jettyBootstrap = initServer(false);
		jettyBootstrap.addHandler(context);
		jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "ServletTestContent" + LINE_SEPARATOR), get("/handler"));
	}

	@Test
	public void do20SslHandlerTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/sslHandler");
		context.addServlet(new ServletHolder(new TestServlet()), "/*");

		JettyBootstrap jettyBootstrap = initServer(true);
		jettyBootstrap.addHandler(context);
		jettyBootstrap.startServer();

		Assert.assertEquals(new SimpleResponse(200, "ServletTestContent" + LINE_SEPARATOR), get("/sslHandler"));
	}

}

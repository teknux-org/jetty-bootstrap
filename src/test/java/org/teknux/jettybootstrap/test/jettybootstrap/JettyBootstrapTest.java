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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.teknux.jettybootstrap.JettyBootstrapException;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JettyBootstrapTest extends AbstractJettyBootstrapTest {

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

		initServer(false).addExplodedWarApp(folder.getPath(), null, "/staticResource").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/staticResource/index.html"));
	}

	@Test
	public void do10SslStaticResourceTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException, URISyntaxException {
		File folder = temporaryFolder.newFolder();
		copyResourceToFile("/webapp", folder);

		initServer(true).addExplodedWarApp(folder.getPath(), null, "/sslStaticResource").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/sslStaticResource/index.html"));
	}

	@Test
	public void do11StaticResourceFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
		initServer(false).addExplodedWarAppFromClasspath("/webapp", null, "/staticResourceFromClasspath").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/staticResourceFromClasspath/index.html"));
	}

	@Test
	public void do12SslStaticResourceFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
		initServer(true).addExplodedWarAppFromClasspath("/webapp", null, "/sslStaticResourceFromClasspath").startServer();

		Assert.assertEquals(new SimpleResponse(200, "StaticResContent\n"), get("/sslStaticResourceFromClasspath/index.html"));
	}

	@Test
	public void do13ExplodedWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
		File folder = temporaryFolder.newFolder();
		copyResourceToFile("/webapp", folder);

		initServer(false).addExplodedWarApp(folder.getPath(), null, "/explodedWar").startServer();

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

}

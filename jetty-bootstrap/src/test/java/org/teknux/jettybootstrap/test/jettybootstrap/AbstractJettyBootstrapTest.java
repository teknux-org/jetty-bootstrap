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
import org.junit.After;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;
import org.teknux.jettybootstrap.JettyBootstrap;
import org.teknux.jettybootstrap.JettyBootstrapException;
import org.teknux.jettybootstrap.configuration.JettyConfiguration;
import org.teknux.jettybootstrap.configuration.JettyConnector;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;


public abstract class AbstractJettyBootstrapTest {

	protected final static String HOST = "127.0.0.1";
	protected final static int PORT = 0;
	protected final static int SSL_PORT = 0;
	protected final static int SOCKET_TIMEOUT = 1000;

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Boolean ssl = null;
	private JettyBootstrap jettyBootstrap = null;

	protected JettyBootstrap initServer(boolean ssl) throws JettyBootstrapException, IOException {
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
		if (jettyBootstrap != null) {
			jettyBootstrap.stopServer();
		}
	}

	protected int getPort() throws JettyBootstrapException {
		if (jettyBootstrap == null) {
			throw new InvalidParameterException("Server not started!");
		}
		// return port jetty is currently running
		return ((ServerConnector) jettyBootstrap.getServer().getConnectors()[0]).getLocalPort();
	}

	protected SimpleResponse get(String url) throws IllegalStateException, IOException, JettyBootstrapException, NoSuchAlgorithmException, KeyStoreException,
			KeyManagementException {
		SimpleResponse simpleResponse = new SimpleResponse();

		CloseableHttpClient httpClient;
		HttpGet httpGet;
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

	protected File copyResourceToFile(String resource, File file) throws IOException, URISyntaxException {
		File fileSrc = new File(getClass().getResource(resource).toURI());

		if (fileSrc.isDirectory()) {
			FileUtils.copyDirectory(fileSrc, file);
		} else {
			FileUtils.copyFile(fileSrc, file);
		}

		return file;
	}
}

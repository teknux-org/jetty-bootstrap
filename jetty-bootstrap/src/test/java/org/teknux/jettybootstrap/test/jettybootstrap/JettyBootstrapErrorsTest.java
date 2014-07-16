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

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;
import org.teknux.jettybootstrap.JettyBootstrap;
import org.teknux.jettybootstrap.JettyBootstrapException;


public class JettyBootstrapErrorsTest extends AbstractJettyBootstrapTest {

    @Test(expected = JettyBootstrapException.class)
	public void missingStaticWarTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException,
			URISyntaxException {
        JettyBootstrap jettyBootstrap = initServer(false);
        jettyBootstrap.addWarApp("Missing", "/staticWar");
        jettyBootstrap.startServer();
	}

	@Test(expected = JettyBootstrapException.class)
	public void missingServletWarFromClasspathTest() throws IllegalStateException, IOException, JettyBootstrapException, KeyManagementException, NoSuchAlgorithmException,
			KeyStoreException {
		// will throw JettyBootstrapException because war is not in classpath
	    JettyBootstrap jettyBootstrap = initServer(false);
	    jettyBootstrap.addWarAppFromClasspath("/servlet-missing.war", "/servletWarFromClasspath-missing");
	    jettyBootstrap.startServer();
	}

}

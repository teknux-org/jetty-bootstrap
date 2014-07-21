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
package org.teknux.jettybootstrap.handler;

import java.security.NoSuchAlgorithmException;

import org.eclipse.jetty.webapp.WebAppContext;
import org.teknux.jettybootstrap.JettyBootstrapException;
import org.teknux.jettybootstrap.configuration.IJettyConfiguration;
import org.teknux.jettybootstrap.utils.Md5Util;


public class WarAppJettyHandler extends AbstractAppJettyHandler {

	public WarAppJettyHandler(IJettyConfiguration iJettyConfiguration) {
        super(iJettyConfiguration);
    }

    private static final String TYPE = "War";

	private String war = null;

	public String getWar() {
		return war;
	}

	public void setWar(String war) {
		this.war = war;
	}

	@Override
	protected String getAppTempDirName() {
		try {
            return Md5Util.hash(getWar());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
	}

	@Override
	protected WebAppContext createHandler() throws JettyBootstrapException {
	    WebAppContext webAppContext = super.createHandler();
		webAppContext.setWar(war);

		return webAppContext;
	}

	@Override
	public String getItemType() {
		return TYPE;
	}

	@Override
	public String getItemName() {
		return war;
	}
}

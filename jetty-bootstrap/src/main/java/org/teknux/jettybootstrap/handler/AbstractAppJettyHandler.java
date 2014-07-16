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

import java.text.MessageFormat;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.teknux.jettybootstrap.JettyBootstrapException;


abstract public class AbstractAppJettyHandler extends AbstractJettyHandler {
	private String contextPath = null;

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

    @Override
	protected Handler createHandler() throws JettyBootstrapException {
		WebAppContext webAppContext = new WebAppContext();

		return initWebAppContext(webAppContext);
	}
	
	/**
	 * The name of Temporary Application directory
	 * 
	 * @return name
	 */
	abstract public String getAppTempDirName();

	abstract protected WebAppContext initWebAppContext(WebAppContext webAppContext);

	@Override
	public String toString() {
		return MessageFormat.format("{0} on contextPath [{1}]", super.toString(), getContextPath());
	}
}

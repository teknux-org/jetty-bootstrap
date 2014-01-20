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

import java.security.InvalidParameterException;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.teknux.jettybootstrap.utils.Md5;


public class StaticResourceAppJettyHandler extends AbstractAppJettyHandler {

	private String webAppBase = null;
	private String webAppBaseFromClasspath = null;

	public String getWebAppBase() {
		return webAppBase;
	}

	public void setWebAppBase(String webAppBase) {
		if (this.webAppBaseFromClasspath != null) {
			throw new InvalidParameterException("You can't set both webAppBase and webAppBaseFromClasspath parameters");
		}

		this.webAppBase = webAppBase;
	}

	public String getWebAppBaseFromClasspath() {
		return webAppBaseFromClasspath;
	}

	public void setWebAppBaseFromClasspath(String webAppBaseFromClasspath) {
		if (this.webAppBase != null) {
			throw new InvalidParameterException("You can't set both webAppBase and webAppBaseFromClasspath parameters");
		}

		this.webAppBaseFromClasspath = webAppBaseFromClasspath;
	}

	@Override
	protected String getAppTempDirName() {
		if (webAppBase != null) {
			return Md5.hash(webAppBase);
		}
		if (webAppBaseFromClasspath != null) {
			return Md5.hash(webAppBaseFromClasspath);
		}

		throw new InvalidParameterException("webAppBase or webAppBaseFromClasspath required");
	}

	@Override
	protected WebAppContext initWebAppContext(WebAppContext webAppContext) {
		if (webAppBase != null) {
			webAppContext.setResourceBase(webAppBase);
		}
		if (webAppBaseFromClasspath != null) {
			webAppContext.setBaseResource(Resource.newClassPathResource(webAppBaseFromClasspath));
		}

		return webAppContext;
	}
}

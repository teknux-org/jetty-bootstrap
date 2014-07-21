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
import java.security.NoSuchAlgorithmException;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.teknux.jettybootstrap.JettyBootstrapException;
import org.teknux.jettybootstrap.configuration.IJettyConfiguration;
import org.teknux.jettybootstrap.utils.Md5Util;


public class ExplodedWarAppJettyHandler extends AbstractAppJettyHandler {

	public ExplodedWarAppJettyHandler(IJettyConfiguration iJettyConfiguration) {
        super(iJettyConfiguration);
    }

    private static final String TYPE = "ExplodedWar";
	private static final String TYPE_FROM_CLASSPATH = "ExplodedWarFromClasspath";

	private String webAppBase = null;
	private String webAppBaseFromClasspath = null;

	private String descriptor = null;

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

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
	public String getAppTempDirName() {
	    try {
    		if (webAppBase != null) {
                return Md5Util.hash(webAppBase);
    		}
    		if (webAppBaseFromClasspath != null) {
    			return Md5Util.hash(webAppBaseFromClasspath);
    		}
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException("Md5 Sum Error", e);
	    }

		throw new InvalidParameterException("webAppBase or webAppBaseFromClasspath required");
	}

	@Override
    public WebAppContext createHandler() throws JettyBootstrapException {
	    WebAppContext webAppContext = super.createHandler();
		if (webAppBase != null) {
			webAppContext.setResourceBase(webAppBase);
		}
		if (webAppBaseFromClasspath != null) {
			webAppContext.setBaseResource(Resource.newClassPathResource(webAppBaseFromClasspath));
		}

		webAppContext.setDescriptor(descriptor);

		return webAppContext;
	}

	@Override
	public String getItemType() {
		if (webAppBase != null) {
			return TYPE;
		}
		if (webAppBaseFromClasspath != null) {
			return TYPE_FROM_CLASSPATH;
		}

		return null;
	}

	@Override
	public String getItemName() {
		if (webAppBase != null) {
			return webAppBase;
		}
		if (webAppBaseFromClasspath != null) {
			if (descriptor == null) {
				return webAppBaseFromClasspath;
			} else {
				return webAppBaseFromClasspath + "(" + descriptor + ")";
			}
		}

		return null;
	}
}

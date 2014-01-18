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

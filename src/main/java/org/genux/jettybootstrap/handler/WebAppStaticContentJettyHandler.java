package org.genux.jettybootstrap.handler;

import java.security.InvalidParameterException;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.genux.jettybootstrap.utils.Md5;


public class WebAppStaticContentJettyHandler extends AbstractWebAppJettyHandler {

	private String webAppBase = null;
	private Resource webAppResourceBase = null;

	public String getWebAppBase() {
		return webAppBase;
	}

	public void setWebAppBase(String webAppBase) {
		if (this.webAppResourceBase != null) {
			throw new InvalidParameterException("You can't set both webAppBase and webAppResourceBase parameters");
		}

		this.webAppBase = webAppBase;
	}

	public Resource getWebAppResourceBase() {
		return webAppResourceBase;
	}

	public void setWebAppResourceBase(String webAppResourceBase) {
		if (this.webAppBase != null) {
			throw new InvalidParameterException("You can't set both webAppBase and webAppResourceBase parameters");
		}

		this.webAppResourceBase = Resource.newClassPathResource(webAppResourceBase);
	}

	@Override
	protected String getAppTempDirName() {
		if (webAppBase != null) {
			return Md5.hash(webAppBase);
		}
		if (webAppResourceBase != null) {
			return Md5.hash(webAppResourceBase.getURI().toString());
		}

		throw new InvalidParameterException("webAppBase or webAppResourceBase required");
	}

	@Override
	protected WebAppContext initWebAppContext(WebAppContext webAppContext) {
		if (webAppBase != null) {
			webAppContext.setResourceBase(webAppBase);
		}
		if (webAppResourceBase != null) {
			webAppContext.setBaseResource(webAppResourceBase);
		}

		return webAppContext;
	}
}

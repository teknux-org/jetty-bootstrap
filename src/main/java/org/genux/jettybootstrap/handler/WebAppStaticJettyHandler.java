package org.genux.jettybootstrap.handler;

import java.io.File;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.genux.jettybootstrap.utils.Md5;


public class WebAppStaticJettyHandler extends AbstractWebAppJettyHandler {

	private Resource baseResource = null;

	public Resource getBaseResource() {
		return baseResource;
	}

	public void setBaseResource(Resource resource) {
		baseResource = resource;
	}

	public void setResourceBase(File file) {
		baseResource = Resource.newResource(file);
	}

	public void setResourceBase(String resource) {
		baseResource = Resource.newClassPathResource(resource);
	}

	@Override
	protected String getAppTempDirName() {
		return Md5.hash(baseResource.getURI().toString());
	}

	@Override
	protected WebAppContext initWebAppContext(WebAppContext webAppContext) {
		webAppContext.setBaseResource(baseResource);

		return webAppContext;
	}
}

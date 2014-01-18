package org.teknux.jettybootstrap.handler;

import org.eclipse.jetty.webapp.WebAppContext;


public class ExplodedWarAppJettyHandler extends StaticResourceAppJettyHandler {

	private String descriptor = null;

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	protected WebAppContext initWebAppContext(WebAppContext webAppContext) {
		super.initWebAppContext(webAppContext);

		webAppContext.setDescriptor(descriptor);

		return webAppContext;
	}
}

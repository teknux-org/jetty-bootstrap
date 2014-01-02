package org.genux.jettybootstrap.handler;

import org.eclipse.jetty.webapp.WebAppContext;


public class WebAppJettyHandler extends WebAppStaticJettyHandler {

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

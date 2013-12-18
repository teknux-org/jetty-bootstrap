package org.genux.jettybootstrap.webApp;

abstract public class AbstractWebApp implements
		IWebApp {

	private String contextPath = null;

	@Override
	public String getContextPath() {
		return contextPath;
	}

	@Override
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
}

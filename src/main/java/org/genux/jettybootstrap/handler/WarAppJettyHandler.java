package org.genux.jettybootstrap.handler;

import org.eclipse.jetty.webapp.WebAppContext;
import org.genux.jettybootstrap.utils.Md5;


public class WarAppJettyHandler extends AbstractAppJettyHandler {

	private String war = null;

	public String getWar() {
		return war;
	}

	public void setWar(String war) {
		this.war = war;
	}

	@Override
	protected String getAppTempDirName() {
		return Md5.hash(getWar());
	}

	@Override
	protected WebAppContext initWebAppContext(WebAppContext webAppContext) {
		webAppContext.setWar(getWar());

		return webAppContext;
	}
}

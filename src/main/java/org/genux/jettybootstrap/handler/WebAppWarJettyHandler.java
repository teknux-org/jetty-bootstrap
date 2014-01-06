package org.genux.jettybootstrap.handler;

import org.eclipse.jetty.webapp.WebAppContext;
import org.genux.jettybootstrap.utils.Md5;


public class WebAppWarJettyHandler extends AbstractWebAppJettyHandler {

	private String warFile = null;

	public String getWarFile() {
		return warFile;
	}

	public void setWarFile(String warFile) {
		this.warFile = warFile;
	}

	@Override
	protected String getAppTempDirName() {
		return Md5.hash(getWarFile());
	}

	@Override
	protected WebAppContext initWebAppContext(WebAppContext webAppContext) {
		webAppContext.setWar(getWarFile());

		return webAppContext;
	}
}

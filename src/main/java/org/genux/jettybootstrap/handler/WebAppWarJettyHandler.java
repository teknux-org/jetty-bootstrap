package org.genux.jettybootstrap.handler;

import java.io.File;

import org.eclipse.jetty.webapp.WebAppContext;
import org.genux.jettybootstrap.utils.Md5;


public class WebAppWarJettyHandler extends AbstractWebAppJettyHandler {

	private File warFile = null;

	public File getWarFile() {
		return warFile;
	}

	public void setWarFile(File warFile) {
		this.warFile = warFile;
	}

	@Override
	protected String getAppTempDirName() {
		return Md5.hash(getWarFile().getPath());
	}

	@Override
	protected WebAppContext initWebAppContext(WebAppContext webAppContext) {
		webAppContext.setWar(getWarFile().getPath());

		return webAppContext;
	}
}

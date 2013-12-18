package org.genux.jettybootstrap.webApp;

import java.io.File;


public class WebAppWar extends AbstractWebApp {

	private File warFile = null;

	public File getWarFile() {
		return warFile;
	}

	public void setWarFile(File warFile) {
		this.warFile = warFile;
	}
}

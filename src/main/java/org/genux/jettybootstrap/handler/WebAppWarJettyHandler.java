package org.genux.jettybootstrap.handler;

import java.io.File;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.webapp.WebAppContext;
import org.genux.jettybootstrap.JettyException;
import org.genux.jettybootstrap.utils.Md5;


public class WebAppWarJettyHandler implements
		IJettyHandler {

	private static final String APP_DIRECTORY_NAME = "apps";

	private String contextPath = null;
	private boolean redirectOnHttpsConnector = false;
	private File tempDirectory = null;
	private boolean persistTempDirectory = false;
	private boolean parentLoaderPriority = true;
	private File warFile = null;

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public boolean isRedirectOnHttpsConnector() {
		return redirectOnHttpsConnector;
	}

	public void setRedirectOnHttpsConnector(boolean redirectOnHttpsConnector) {
		this.redirectOnHttpsConnector = redirectOnHttpsConnector;
	}

	public File getTempDirectory() {
		return tempDirectory;
	}

	public void setTempDirectory(File tempDirectory) {
		this.tempDirectory = tempDirectory;
	}

	public boolean isPersistTempDirectory() {
		return persistTempDirectory;
	}

	public void setPersistTempDirectory(boolean persistTempDirectory) {
		this.persistTempDirectory = persistTempDirectory;
	}

	public boolean isParentLoaderPriority() {
		return parentLoaderPriority;
	}

	public void setParentLoaderPriority(boolean parentLoaderPriority) {
		this.parentLoaderPriority = parentLoaderPriority;
	}

	public File getWarFile() {
		return warFile;
	}

	public void setWarFile(File warFile) {
		this.warFile = warFile;
	}

	@Override
	public Handler getHandler() throws JettyException {
		File appsTempDirectory = new File(getTempDirectory() + File.separator + APP_DIRECTORY_NAME);

		if (!appsTempDirectory.exists()) {
			if (!appsTempDirectory.mkdir()) {
				throw new JettyException("Can't create temporary applications directory");
			}
		}

		String dirName = Md5.hash(getWarFile().getPath());
		File appTempDirectory = new File(appsTempDirectory.getPath() + File.separator + dirName);

		WebAppContext webAppContext = new WebAppContext(getWarFile().getPath(), getContextPath());

		if (isRedirectOnHttpsConnector()) {
			webAppContext.setSecurityHandler(getConstraintSecurityHandlerConfidential());
		}

		webAppContext.setTempDirectory(appTempDirectory);
		webAppContext.setParentLoaderPriority(isParentLoaderPriority());
		webAppContext.setPersistTempDirectory(isPersistTempDirectory());

		return webAppContext;
	}

	/**
	 * Create constraint which redirect to Secure Port
	 * 
	 * @return @ConstraintSecurityHandler
	 */
	private ConstraintSecurityHandler getConstraintSecurityHandlerConfidential() {
		Constraint constraint = new Constraint();
		constraint.setDataConstraint(Constraint.DC_CONFIDENTIAL);

		ConstraintMapping constraintMapping = new ConstraintMapping();
		constraintMapping.setConstraint(constraint);
		constraintMapping.setPathSpec("/*");

		ConstraintSecurityHandler constraintSecurityHandler = new ConstraintSecurityHandler();
		constraintSecurityHandler.addConstraintMapping(constraintMapping);

		return constraintSecurityHandler;
	}
}

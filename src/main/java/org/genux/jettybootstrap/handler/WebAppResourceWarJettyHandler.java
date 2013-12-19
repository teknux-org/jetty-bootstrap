package org.genux.jettybootstrap.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Handler;
import org.genux.jettybootstrap.JettyBootstrap;
import org.genux.jettybootstrap.JettyBootstrapException;
import org.genux.jettybootstrap.utils.Md5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WebAppResourceWarJettyHandler extends WebAppWarJettyHandler {

	private final Logger logger = LoggerFactory.getLogger(WebAppResourceWarJettyHandler.class);

	private static final String RESOURCEWAR_DIRECTORY_NAME = "war";
	private static final String WAR_EXTENSION = ".war";

	private String resource = null;

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	@Override
	public Handler getHandler() throws JettyBootstrapException {
		File resourcesWarDirectory = new File(getTempDirectory().getPath() + File.separator + RESOURCEWAR_DIRECTORY_NAME);

		if (!resourcesWarDirectory.exists() && !resourcesWarDirectory.mkdir()) {
			throw new JettyBootstrapException("Can't create temporary resources war directory");
		}

		String fileName = Md5.hash(getResource()) + WAR_EXTENSION;
		File resourceWarFile = new File(resourcesWarDirectory.getPath() + File.separator + fileName);

		if (resourceWarFile.exists()) {
			logger.trace("War resource already exists in directory : [{}], don't copy", resourcesWarDirectory);
		} else {
			logger.trace("Copy war resource [{}] to directory : [{}]...", getResource(), resourcesWarDirectory);

			InputStream inputStream = null;
			FileOutputStream fileOutputStream = null;
			try {
				inputStream = JettyBootstrap.class.getResourceAsStream(getResource());
				fileOutputStream = new FileOutputStream(resourceWarFile);
				IOUtils.copy(inputStream, fileOutputStream);
			} catch (FileNotFoundException e) {
				throw new JettyBootstrapException(e);
			} catch (IOException e) {
				throw new JettyBootstrapException(e);
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
				} catch (IOException e) {
					logger.error("Can't closed streams on deployResource", e);
				}
			}
		}

		setWarFile(resourceWarFile);

		return super.getHandler();
	}
}

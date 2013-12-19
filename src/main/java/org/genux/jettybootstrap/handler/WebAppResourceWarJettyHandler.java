package org.genux.jettybootstrap.handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.genux.jettybootstrap.JettyBootstrap;
import org.genux.jettybootstrap.JettyException;
import org.genux.jettybootstrap.utils.Md5;


public class WebAppResourceWarJettyHandler extends WebAppWarJettyHandler {

	private static final Logger LOGGER = Logger.getLogger(WebAppResourceWarJettyHandler.class);

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
	public Handler getHandler() throws JettyException {
		File resourcesWarDirectory = new File(getTempDirectory().getPath() + File.separator + RESOURCEWAR_DIRECTORY_NAME);

		if (!resourcesWarDirectory.exists()) {
			if (!resourcesWarDirectory.mkdir()) {
				throw new JettyException("Can't create temporary resources war directory");
			}
		}

		String fileName = Md5.hash(getResource()) + WAR_EXTENSION;
		File resourceWarFile = new File(resourcesWarDirectory.getPath() + File.separator + fileName);

		if (resourceWarFile.exists()) {
			LOGGER.trace(MessageFormat.format("War resource already exists in directory : [{0}], don't copy", resourcesWarDirectory));
		} else {
			LOGGER.trace(MessageFormat.format("Copy war resource [{0}] to directory : [{1}]...", getResource(), resourcesWarDirectory));

			InputStream inputStream = null;
			FileOutputStream fileOutputStream = null;
			try {
				inputStream = JettyBootstrap.class.getResourceAsStream(getResource());
				fileOutputStream = new FileOutputStream(resourceWarFile);
				IOUtils.copy(inputStream, fileOutputStream);
			} catch (FileNotFoundException e) {
				throw new JettyException(e);
			} catch (IOException e) {
				throw new JettyException(e);
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
				} catch (IOException e) {
					LOGGER.error("Can't closed streams on deployResource", e);
				}
			}
		}

		setWarFile(resourceWarFile);

		return super.getHandler();
	}
}

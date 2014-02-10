/*******************************************************************************
 * (C) Copyright 2014 Teknux.org (http://teknux.org/).
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *  
 * Contributors:
 *      "Pierre PINON"
 *      "Francois EYL"
 *      "Laurent MARCHAL"
 *  
 *******************************************************************************/
package org.teknux.jettybootstrap.handler;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teknux.jettybootstrap.JettyBootstrapException;
import org.teknux.jettybootstrap.utils.Md5;


public class WarAppFromClasspathJettyHandler extends WarAppJettyHandler {

	private static final String TYPE = "WarFromClasspath";

	private final Logger logger = LoggerFactory.getLogger(WarAppFromClasspathJettyHandler.class);

	private static final String RESOURCEWAR_DIRECTORY_NAME = "war";
	private static final String WAR_EXTENSION = ".war";

	private String warFromClasspath = null;

	public String getWarFromClasspath() {
		return warFromClasspath;
	}

	public void setWarFromClasspath(String resourceWar) {
		this.warFromClasspath = resourceWar;
	}

	@Override
	public Handler getHandler() throws JettyBootstrapException {
		File warDirectory = new File(getTempDirectory().getPath() + File.separator + RESOURCEWAR_DIRECTORY_NAME);

		if (!warDirectory.exists() && !warDirectory.mkdir()) {
			throw new JettyBootstrapException("Can't create temporary War directory");
		}

		String fileName = Md5.hash(getWarFromClasspath()) + WAR_EXTENSION;
		File warFile = new File(warDirectory.getPath() + File.separator + fileName);

		if (warFile.exists()) {
			logger.trace("War already exists in directory : [{}], don't copy", warDirectory);
		} else {
			logger.trace("Copy war from classpath [{}] to directory [{}]...", getWarFromClasspath(), warDirectory);

			try {
				File classpathFile = new File(getClass().getResource(getWarFromClasspath()).toURI());

				FileUtils.copyFile(classpathFile, warFile);
			} catch (URISyntaxException e) {
				throw new JettyBootstrapException(e);
			} catch (IOException e) {
				throw new JettyBootstrapException(e);
			}
		}

		setWar(warFile.getPath());

		return super.getHandler();
	}

	@Override
	public String getItemType() {
		return TYPE;
	}

	@Override
	public String getItemName() {
		return warFromClasspath;
	}
}

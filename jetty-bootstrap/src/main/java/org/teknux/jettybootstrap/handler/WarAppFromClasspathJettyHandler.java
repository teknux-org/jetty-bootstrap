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
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teknux.jettybootstrap.JettyBootstrapException;
import org.teknux.jettybootstrap.configuration.IJettyConfiguration;
import org.teknux.jettybootstrap.utils.Md5Util;


public class WarAppFromClasspathJettyHandler extends WarAppJettyHandler {

    public WarAppFromClasspathJettyHandler(IJettyConfiguration iJettyConfiguration) {
        super(iJettyConfiguration);
    }

    private static final String TYPE = "WarFromClasspath";

    private static final Logger LOG = LoggerFactory.getLogger(WarAppFromClasspathJettyHandler.class);

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
    protected WebAppContext createHandler() throws JettyBootstrapException {
        File warDirectory = new File(getJettyConfiguration().getTempDirectory().getPath() + File.separator + RESOURCEWAR_DIRECTORY_NAME);

        if (!warDirectory.exists() && !warDirectory.mkdir()) {
            throw new JettyBootstrapException("Can't create temporary War directory");
        }

        String fileName;
        try {
            fileName = Md5Util.hash(warFromClasspath) + WAR_EXTENSION;
        } catch (NoSuchAlgorithmException e) {
            throw new JettyBootstrapException(e);
        }
        File warFile = new File(warDirectory.getPath() + File.separator + fileName);

        if (warFile.exists()) {
            LOG.trace("War already exists in directory : [{}], not copied", warDirectory);
        } else {
            LOG.trace("Copy war file from classpath [{}] to directory [{}]...", warFromClasspath, warDirectory);

            try (InputStream inputStream = getClass().getResourceAsStream(warFromClasspath)) {
                if (inputStream == null) {
                    throw new JettyBootstrapException("Cannot get resource as stream from classpath : " + warFromClasspath);
                }
                FileUtils.copyInputStreamToFile(inputStream, warFile);
            } catch (IOException e) {
                throw new JettyBootstrapException(e);
            }
        }

        setWar(warFile.getPath());

        return super.createHandler();
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

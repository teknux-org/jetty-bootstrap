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
package org.teknux.jettybootstrap.standalone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teknux.jettybootstrap.JettyBootstrap;
import org.teknux.jettybootstrap.JettyBootstrapException;

import java.io.File;


/**
 * Convenient class to use JettyBootstrap JAR directly from command line and deploy an existing application(s) from the host file system on a basic server.
 */
public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static final String WAR_FILE_SUFFIX = ".war";

    /**
     * @param args
     *            each argument is a web application (war file, directory).
     * @throws JettyBootstrapException
     *             on failure
     */
    public static void main(String[] args) throws JettyBootstrapException {
        if (args.length == 0) {
            LOG.warn("Nothing to deploy, Exiting...");
        } else {
            JettyBootstrap jettyBootstrap = new JettyBootstrap();

            for (String arg : args) {
                File file = new File(arg);

                if (!file.exists()) {
                    LOG.warn("File [{}] doesn't exists. Ignore application", file);
                } else {

                    String fileName;
                    if (file.isFile() && file.getName().toLowerCase().endsWith(WAR_FILE_SUFFIX)) {
                        fileName = file.getName().substring(0, file.getName().length() - WAR_FILE_SUFFIX.length());
                    } else {
                        fileName =  file.getName();
                    }

                    String contextPath = "/";
                    if (! fileName.equals("ROOT")) {
                        contextPath += fileName;
                    }

                    if (file.isDirectory()) {
                        LOG.debug("[{}] exists and is a directory. Adding Exploded War Application...", file);
                        jettyBootstrap.addExplodedWarApp(file.getPath(), null, contextPath);
                    } else {
                        if (file.isFile() && file.getName().toLowerCase().endsWith(".war")) {
                            LOG.debug("[{}] exists and is a war file. Add War Application...", file);
                            jettyBootstrap.addWarApp(file.getPath(), contextPath);
                        } else {
                            LOG.warn("[{}] exists but is an unknown file. Ignore application", file);
                        }
                    }
                }
            }

            jettyBootstrap.startServer();
        }
    }
}

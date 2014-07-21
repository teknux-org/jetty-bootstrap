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
import java.text.MessageFormat;

import org.eclipse.jetty.webapp.WebAppContext;
import org.teknux.jettybootstrap.JettyBootstrapException;
import org.teknux.jettybootstrap.configuration.IJettyConfiguration;
import org.teknux.jettybootstrap.handler.util.AdditionalWebAppJettyConfigurationUtil;
import org.teknux.jettybootstrap.handler.util.JettyConstraintUtil;

abstract public class AbstractAppJettyHandler extends AbstractJettyHandler<WebAppContext> {
    private static final String APP_DIRECTORY_NAME = "apps";

    private IJettyConfiguration iJettyConfiguration;
    private String contextPath = null;

    public AbstractAppJettyHandler(IJettyConfiguration iJettyConfiguration) {
        this.iJettyConfiguration = iJettyConfiguration;
    }

    protected IJettyConfiguration getJettyConfiguration() {
        return iJettyConfiguration;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    @Override
    protected WebAppContext createHandler() throws JettyBootstrapException {
        WebAppContext webAppContext = new WebAppContext();

        // Init WebAppContext from Jetty Configuration
        webAppContext.setParentLoaderPriority(iJettyConfiguration.isParentLoaderPriority());
        webAppContext.setPersistTempDirectory(iJettyConfiguration.isPersistAppTempDirectories());
        webAppContext.setThrowUnavailableOnStartupException(iJettyConfiguration.isThrowIfStartupException());
        webAppContext.getSessionHandler().getSessionManager().setMaxInactiveInterval(iJettyConfiguration.getMaxInactiveInterval());

        // Add redirect to SSL if necessary
        if (iJettyConfiguration.isRedirectWebAppsOnHttpsConnector()) {
            webAppContext.setSecurityHandler(JettyConstraintUtil.getConstraintSecurityHandlerConfidential());
        }

        // Init temp directory
        File appsTempDirectory = new File(iJettyConfiguration.getTempDirectory() + File.separator + APP_DIRECTORY_NAME);
        if (!appsTempDirectory.exists() && !appsTempDirectory.mkdir()) {
            throw new JettyBootstrapException("Can't create temporary applications directory");
        }
        File appTempDirectory = new File(appsTempDirectory.getPath() + File.separator + getAppTempDirName());
        webAppContext.setTempDirectory(appTempDirectory);

        // Adds extra classes if necessary
        webAppContext.setConfigurationClasses(AdditionalWebAppJettyConfigurationUtil.addOptionalConfigurationClasses(WebAppContext
                .getDefaultConfigurationClasses()));

        // Set Context Path
        webAppContext.setContextPath(contextPath);

        return webAppContext;
    }

    /**
     * The name of Temporary Application directory
     * 
     * @return name
     */
    abstract protected String getAppTempDirName();

    @Override
    public String toString() {
        return MessageFormat.format("{0} on contextPath [{1}]", super.toString(), getContextPath());
    }
}

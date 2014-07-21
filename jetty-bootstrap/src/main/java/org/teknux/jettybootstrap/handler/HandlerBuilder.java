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

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.teknux.jettybootstrap.JettyBootstrapException;
import org.teknux.jettybootstrap.configuration.IJettyConfiguration;
import org.teknux.jettybootstrap.handler.util.AdditionalWebAppJettyConfigurationUtil;
import org.teknux.jettybootstrap.handler.util.JettyConstraintUtil;
import org.teknux.jettybootstrap.handler.util.JettyLifeCycleLogListener;

public class HandlerBuilder {
    
    private static final String APP_DIRECTORY_NAME = "apps";

    private IJettyConfiguration iJettyConfiguration;
    private IJettyHandler iJettyHandler;

    public HandlerBuilder(IJettyConfiguration iJettyConfiguration, IJettyHandler iJettyHandler) {
        this.iJettyConfiguration = iJettyConfiguration;
        this.iJettyHandler = iJettyHandler;
    }
    
    public Handler build() throws JettyBootstrapException {
        Handler handler = iJettyHandler.getHandler();
        if (iJettyHandler instanceof AbstractAppJettyHandler) {
            WebAppContext webAppContext = (WebAppContext) handler;
            webAppContext.setParentLoaderPriority(iJettyConfiguration.isParentLoaderPriority());
            webAppContext.setPersistTempDirectory(iJettyConfiguration.isPersistAppTempDirectories());
            webAppContext.setConfigurationClasses(AdditionalWebAppJettyConfigurationUtil.addOptionalConfigurationClasses(WebAppContext.getDefaultConfigurationClasses()));
            webAppContext.setThrowUnavailableOnStartupException(iJettyConfiguration.isThrowIfStartupException());
            webAppContext.getSessionHandler().getSessionManager().setMaxInactiveInterval(iJettyConfiguration.getMaxInactiveInterval());

            if (iJettyConfiguration.isRedirectWebAppsOnHttpsConnector()) {
                webAppContext.setSecurityHandler(JettyConstraintUtil.getConstraintSecurityHandlerConfidential());
            }
            
            AbstractAppJettyHandler abstractAppJettyHandler = (AbstractAppJettyHandler)iJettyHandler;           
            webAppContext.setContextPath(abstractAppJettyHandler.getContextPath());

            File appsTempDirectory = new File(iJettyConfiguration.getTempDirectory() + File.separator + APP_DIRECTORY_NAME);
            if (!appsTempDirectory.exists() && !appsTempDirectory.mkdir()) {
                throw new JettyBootstrapException("Can't create temporary applications directory");
            }
            File appTempDirectory = new File(appsTempDirectory.getPath() + File.separator + abstractAppJettyHandler.getAppTempDirName());
            webAppContext.setTempDirectory(appTempDirectory);

            if (iJettyHandler instanceof ExplodedWarAppJettyHandler) {
                ExplodedWarAppJettyHandler explodedWarAppJettyHandler = (ExplodedWarAppJettyHandler)iJettyHandler;
                webAppContext.setDescriptor(explodedWarAppJettyHandler.getDescriptor());
            }
        }
        
        handler.addLifeCycleListener(new JettyLifeCycleLogListener(iJettyHandler));
        
        return handler;
    }
}

package org.teknux.jettybootstrap.handler;

import java.io.File;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.webapp.WebAppContext;
import org.teknux.jettybootstrap.JettyBootstrapException;
import org.teknux.jettybootstrap.configuration.AdditionalWebAppJettyConfigurationClass;
import org.teknux.jettybootstrap.configuration.IJettyConfiguration;
import org.teknux.jettybootstrap.utils.JettyConstraintUtil;

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
            webAppContext.setConfigurationClasses(AdditionalWebAppJettyConfigurationClass.addConfigurationClasses(WebAppContext.getDefaultConfigurationClasses(), AdditionalWebAppJettyConfigurationClass.getAdditionalsWebAppJettyConfigurationClasses()));
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
        return handler;
    }
}

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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teknux.jettybootstrap.JettyBootstrapException;
import org.teknux.jettybootstrap.configuration.AdditionalWebAppJettyConfigurationClass;
import org.teknux.jettybootstrap.configuration.AdditionalWebAppJettyConfigurationClass.Position;


abstract public class AbstractAppJettyHandler extends AbstractJettyHandler {
	
	private final static Logger logger = LoggerFactory.getLogger(AbstractAppJettyHandler.class);

	private String contextPath = null;

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

    @Override
	protected Handler createHandler() throws JettyBootstrapException {
		WebAppContext webAppContext = new WebAppContext();

		return initWebAppContext(webAppContext);
	}

	public static String[] addConfigurationClasses(String[] defaultConfigurationClasses, AdditionalWebAppJettyConfigurationClass[] additionalsWebappConfigurationClasses) {
		
		List<String> configurationClasses = new ArrayList<String>(Arrays.asList(defaultConfigurationClasses));
		
		for (AdditionalWebAppJettyConfigurationClass additionalWebappConfigurationClass : additionalsWebappConfigurationClasses) {
			if (additionalWebappConfigurationClass.getClasses() == null || additionalWebappConfigurationClass.getPosition() == null) {
				logger.warn("Bad support class name");
			} else {
				if (classesExists(additionalWebappConfigurationClass.getClasses())) {
					int index = 0;
					
					if (additionalWebappConfigurationClass.getReferenceClass() == null) {
						if (additionalWebappConfigurationClass.getPosition() == Position.AFTER) {
							index = configurationClasses.size();
						}
					} else {
						index = configurationClasses.indexOf(additionalWebappConfigurationClass.getReferenceClass());
						
						if (index == -1) {
							if (additionalWebappConfigurationClass.getPosition() == Position.AFTER) {
								logger.warn("[{}] reference unreachable, add at the end", additionalWebappConfigurationClass.getReferenceClass());
								index = configurationClasses.size();
							} else {
								logger.warn("[{}] reference unreachable, add at the top", additionalWebappConfigurationClass.getReferenceClass());
								index = 0;
							}
						} else {
							if (additionalWebappConfigurationClass.getPosition() == Position.AFTER) {
								index++;
							}
						}
					}
					
					configurationClasses.addAll(index, additionalWebappConfigurationClass.getClasses());
					
					for (String className : additionalWebappConfigurationClass.getClasses()) {
						logger.debug("[{}] support added", className);
					}
				} else {
					for (String className : additionalWebappConfigurationClass.getClasses()) {
						logger.debug("[{}] not available", className);
					}
				}
			}
		}

		// List configurations
		for (String configurationClasse : configurationClasses) {
			logger.trace("Jetty WebAppContext Configuration => " + configurationClasse);
		}

		return configurationClasses.toArray(new String[configurationClasses.size()]);
	}
	
	public static boolean classesExists(List<String> classNames) {
		for (String className : classNames) {
			try {
				Class.forName(className);
			} catch (ClassNotFoundException e) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * The name of Temporary Application directory
	 * 
	 * @return name
	 */
	abstract public String getAppTempDirName();

	abstract protected WebAppContext initWebAppContext(WebAppContext webAppContext);

	/**
	 * Create constraint which redirect to Secure Port
	 * 
	 * @return @ConstraintSecurityHandler
	 */
	public static ConstraintSecurityHandler getConstraintSecurityHandlerConfidential() {
		Constraint constraint = new Constraint();
		constraint.setDataConstraint(Constraint.DC_CONFIDENTIAL);

		ConstraintMapping constraintMapping = new ConstraintMapping();
		constraintMapping.setConstraint(constraint);
		constraintMapping.setPathSpec("/*");

		ConstraintSecurityHandler constraintSecurityHandler = new ConstraintSecurityHandler();
		constraintSecurityHandler.addConstraintMapping(constraintMapping);

		return constraintSecurityHandler;
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0} on contextPath [{1}]", super.toString(), getContextPath());
	}
}

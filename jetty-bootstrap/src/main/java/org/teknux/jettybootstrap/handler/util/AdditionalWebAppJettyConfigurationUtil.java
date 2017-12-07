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

package org.teknux.jettybootstrap.handler.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teknux.jettybootstrap.handler.util.AdditionalWebAppJettyConfigurationClass.Position;
import org.teknux.jettybootstrap.utils.ClassUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdditionalWebAppJettyConfigurationUtil {
    private final static Logger LOG = LoggerFactory.getLogger(AdditionalWebAppJettyConfigurationUtil.class);
    
    /**
     * Classes that natively supported by JettyBootstrap
     *  
     * @return String[]
     */
    private static AdditionalWebAppJettyConfigurationClass[] getOptionnalAdditionalsWebAppJettyConfigurationClasses() {
        return new AdditionalWebAppJettyConfigurationClass[] {
            new AdditionalWebAppJettyConfigurationClass("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", Position.BEFORE, "org.eclipse.jetty.annotations.AnnotationConfiguration")
        };
    }

    /**
     * Add the classes that natively supported by JettyBootstrap if available (e.g. : Annotations)
     * 
     * @param configurationClasses Class Name array
     * @return String[]
     */
    public static String[] addOptionalConfigurationClasses(String[] configurationClasses) {
        return addConfigurationClasses(configurationClasses, AdditionalWebAppJettyConfigurationUtil.getOptionnalAdditionalsWebAppJettyConfigurationClasses());
    }
    
    /**
     * Add the optionalAdditionalsWebappConfigurationClasses to the configurationClasses if available
     * 
     * @param configurationClasses Class Name array
     * @param optionalAdditionalsWebappConfigurationClasses Class Name array
     * @return String[]
     */
    public static String[] addConfigurationClasses(String[] configurationClasses, AdditionalWebAppJettyConfigurationClass[] optionalAdditionalsWebappConfigurationClasses) {
        List<String> newConfigurationClasses = new ArrayList<>(Arrays.asList(configurationClasses));
        
        for (AdditionalWebAppJettyConfigurationClass additionalWebappConfigurationClass : optionalAdditionalsWebappConfigurationClasses) {
            if (additionalWebappConfigurationClass.getClasses() == null || additionalWebappConfigurationClass.getPosition() == null) {
                LOG.warn("Bad support class name");
            } else {
                if (ClassUtil.classesExists(additionalWebappConfigurationClass.getClasses())) {
                    int index = 0;
                    
                    if (additionalWebappConfigurationClass.getReferenceClass() == null) {
                        if (additionalWebappConfigurationClass.getPosition() == Position.AFTER) {
                            index = newConfigurationClasses.size();
                        }
                    } else {
                        index = newConfigurationClasses.indexOf(additionalWebappConfigurationClass.getReferenceClass());
                        
                        if (index == -1) {
                            if (additionalWebappConfigurationClass.getPosition() == Position.AFTER) {
                                LOG.warn("[{}] reference unreachable, add at the end", additionalWebappConfigurationClass.getReferenceClass());
                                index = newConfigurationClasses.size();
                            } else {
                                LOG.warn("[{}] reference unreachable, add at the top", additionalWebappConfigurationClass.getReferenceClass());
                                index = 0;
                            }
                        } else {
                            if (additionalWebappConfigurationClass.getPosition() == Position.AFTER) {
                                index++;
                            }
                        }
                    }
                    
                    newConfigurationClasses.addAll(index, additionalWebappConfigurationClass.getClasses());
                    
                    for (String className : additionalWebappConfigurationClass.getClasses()) {
                        LOG.debug("[{}] support added", className);
                    }
                } else {
                    for (String className : additionalWebappConfigurationClass.getClasses()) {
                        LOG.debug("[{}] not available", className);
                    }
                }
            }
        }

        // List configurations
        for (String configurationClasse : newConfigurationClasses) {
            LOG.trace("Jetty WebAppContext Configuration => " + configurationClasse);
        }

        return newConfigurationClasses.toArray(new String[newConfigurationClasses.size()]);
    }
}

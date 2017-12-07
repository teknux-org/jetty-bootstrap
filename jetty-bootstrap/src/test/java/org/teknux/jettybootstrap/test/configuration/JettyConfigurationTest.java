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
package org.teknux.jettybootstrap.test.configuration;

import org.junit.Assert;
import org.junit.Test;
import org.teknux.jettybootstrap.configuration.IJettyConfiguration;
import org.teknux.jettybootstrap.configuration.JettyConfiguration;
import org.teknux.jettybootstrap.configuration.JettyConnector;

import java.util.HashSet;
import java.util.Set;


public class JettyConfigurationTest {

    @Test
    public void cloneTest() throws CloneNotSupportedException {
        JettyConfiguration jettyConfiguration = new JettyConfiguration();
        jettyConfiguration.setAutoJoinOnStart(true);
        jettyConfiguration.setHost("0.0.0.0");
        jettyConfiguration.setJettyConnectors(JettyConnector.HTTPS);
        
        IJettyConfiguration iJettyConfigurationCloned = jettyConfiguration.clone();
        
        jettyConfiguration.setAutoJoinOnStart(false);
        jettyConfiguration.setHost("127.0.0.1");
        jettyConfiguration.setJettyConnectors(JettyConnector.HTTP);
        
        Assert.assertEquals(false, jettyConfiguration.isAutoJoinOnStart());
        Assert.assertEquals("127.0.0.1", jettyConfiguration.getHost());
        Set<JettyConnector> expectedJettyConnectors = new HashSet<>();
        expectedJettyConnectors.add(JettyConnector.HTTP);
        Assert.assertEquals(expectedJettyConnectors, jettyConfiguration.getJettyConnectors());
        
        Assert.assertEquals(true, iJettyConfigurationCloned.isAutoJoinOnStart());
        Assert.assertEquals("0.0.0.0", iJettyConfigurationCloned.getHost());
        Set<JettyConnector> expectedJettyConnectorsCloned = new HashSet<>();
        expectedJettyConnectorsCloned.add(JettyConnector.HTTPS);
        Assert.assertEquals(expectedJettyConnectorsCloned, iJettyConfigurationCloned.getJettyConnectors());
    }
}

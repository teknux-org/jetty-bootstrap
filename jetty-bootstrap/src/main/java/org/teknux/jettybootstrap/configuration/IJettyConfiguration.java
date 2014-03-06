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
package org.teknux.jettybootstrap.configuration;

import java.io.File;
import java.util.Set;


/**
 * This interface represents all the available configuration options for the jetty server
 */
public interface IJettyConfiguration {

	boolean isAutoJoinOnStart();

	void setAutoJoinOnStart(boolean autoJoinOnStart);

	int getMaxThreads();

	void setMaxThreads(int maxThreads);

	boolean isStopAtShutdown();

	void setStopAtShutdown(boolean stopAtShutdown);

	long getStopTimeout();

	void setStopTimeout(long stopTimeout);

	long getIdleTimeout();

	void setIdleTimeout(long idleTimeout);

	String getHost();

	void setHost(String host);

	int getPort();

	void setPort(int port);

	int getSslPort();

	void setSslPort(int sslPort);

	boolean hasJettyConnector(JettyConnector jettyConnector);

	Set<JettyConnector> getJettyConnectors();

	void setJettyConnectors(JettyConnector... jettyConnectors);

	boolean isRedirectWebAppsOnHttpsConnector();

	void setRedirectWebAppsOnHttpsConnector(boolean redirectWebAppsOnHttpsConnector);

	String getSslKeyStorePassword();

	void setSslKeyStorePassword(String sslKeyStorePassword);

	String getSslKeyStorePath();

	void setSslKeyStorePath(String sslKeyStorePath);

	File getTempDirectory();

	void setTempDirectory(File tempDirectory);

	boolean isPersistAppTempDirectories();

	void setPersistAppTempDirectories(boolean persistTempDirectory);

	boolean isCleanTempDir();

	void setCleanTempDir(boolean cleanTempDir);

	boolean isParentLoaderPriority();

	void setParentLoaderPriority(boolean parentLoaderPriority);
}

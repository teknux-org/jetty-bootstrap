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

import org.eclipse.jetty.server.AbstractConnector;


/**
 * This interface represents all the available configuration
 * options for the jetty server
 */
public interface IJettyConfiguration {

	/**
	 * @return <code>true</code> to join jetty with main
	 *         thread.
	 */
	boolean isAutoJoinOnStart();

	/**
	 * Set whether or not main thread should join jetty
	 * ones.
	 * 
	 * @param autoJoinOnStart
	 */
	void setAutoJoinOnStart(boolean autoJoinOnStart);

	/**
	 * @return maximum number of threads jetty is allowed
	 *         handle.
	 */
	int getMaxThreads();

	/**
	 * Set the maximum number of threads jetty is allowed
	 * handle.
	 * 
	 * @param maxThreads
	 */
	void setMaxThreads(int maxThreads);

	/**
	 * Is jetty server must be stopped whenever the
	 * jettybootstrap stops. Otherwise jetty remains
	 * running.
	 * 
	 * @return <code>true</code> to stop jetty at
	 *         application stop. <code>false</code> to keep
	 *         jetty running after the application life.
	 */
	boolean isStopAtShutdown();

	/**
	 * Set whether or not the jetty server must be stopped
	 * whenever the main application stops. Otherwise jetty
	 * remains running.
	 * 
	 * @param stopAtShutdown
	 */
	void setStopAtShutdown(boolean stopAtShutdown);

	/**
	 * @return the timeout before forcing jetty to stop.
	 */
	long getStopTimeout();

	void setStopTimeout(long stopTimeout);

	/**
	 * Get the connectors max idle time for a connection.
	 * This applies to all connectors.
	 * 
	 * @see AbstractConnector#setIdleTimeout
	 * @return the idle time.
	 */
	long getIdleTimeout();

	/**
	 * Set the connectors max idle time for a connection.
	 * This applies to all connectors.
	 * 
	 * @see AbstractConnector#setIdleTimeout
	 * @param idleTimeout
	 */
	void setIdleTimeout(long idleTimeout);

	/**
	 * Get the server host IP/Name.
	 * 
	 * @return
	 */
	String getHost();

	/**
	 * Set the server host IP/Name.
	 * 
	 * @param host
	 */
	void setHost(String host);

	/**
	 * Get the HTTP port number.
	 * 
	 * @return
	 */
	int getPort();

	/**
	 * Set the HTTP port number.
	 * 
	 * @param port
	 */
	void setPort(int port);

	/**
	 * Get the port number for SSL (HTTPS).
	 * 
	 * @return
	 */
	int getSslPort();

	/**
	 * Set the port number used for SSL (HTTPS).
	 * 
	 * @param sslPort
	 */
	void setSslPort(int sslPort);

	/**
	 * Check whether or not the given {@link JettyConnector}
	 * has to be supported by the server.
	 * 
	 * @param jettyConnector
	 * @return
	 */
	boolean hasJettyConnector(JettyConnector jettyConnector);

	/**
	 * Get the collection of connectors the server as to
	 * use.
	 * 
	 * @return
	 */
	Set<JettyConnector> getJettyConnectors();

	/**
	 * Set what connectors the server has to use.
	 * 
	 * @param jettyConnectors
	 */
	void setJettyConnectors(JettyConnector... jettyConnectors);

	/**
	 * @return <code>true</code> if all http requests have
	 *         to be redirected to https. <code>false</code>
	 *         otherwise.
	 */
	boolean isRedirectWebAppsOnHttpsConnector();

	/**
	 * Set whether or not the server must redirect http
	 * request to https.
	 * 
	 * @param redirectWebAppsOnHttpsConnector
	 *            <code>true</code> to redirect all http
	 *            request to https. <code>false</code>
	 *            otherwise.
	 */
	void setRedirectWebAppsOnHttpsConnector(boolean redirectWebAppsOnHttpsConnector);

	/**
	 * Get the password of the SSL keystore file.
	 * 
	 * @return the SSL keystore file password
	 */
	String getSslKeyStorePassword();

	/**
	 * Set the password of the SSL keystore file.
	 * 
	 * @param sslKeyStorePassword
	 */
	void setSslKeyStorePassword(String sslKeyStorePassword);

	/**
	 * Get the SSL keystore file path, used for HTTPS
	 * support.
	 * 
	 * @return the path of the SSL keystore file.
	 */
	String getSslKeyStorePath();

	/**
	 * Set the SSL keystore file path used for HTTPS
	 * support.
	 * 
	 * @param sslKeyStorePath
	 */
	void setSslKeyStorePath(String sslKeyStorePath);

	/**
	 * @return the temporary directory used to deploy
	 *         applications.
	 */
	File getTempDirectory();

	/**
	 * Set the temporary directory used to deploy
	 * applications.
	 * 
	 * @param tempDirectory
	 */
	void setTempDirectory(File tempDirectory);

	/**
	 * @return <code>true</code> in case the applications
	 *         temporary directory contents must be
	 *         preserved over server stop/start.
	 *         <code>false</code> otherwise.
	 */
	boolean isPersistAppTempDirectories();

	/**
	 * Set whether or not the applications temporary
	 * directory must be preserved over restart.
	 * 
	 * @param persistTempDirectory
	 */
	void setPersistAppTempDirectories(boolean persistTempDirectory);

	/**
	 * @return <code>true</code> in case the server has to
	 *         clear the temp directory before starting.
	 */
	boolean isCleanTempDir();

	/**
	 * Set whether or not a clear of the temp directory is
	 * needed before starting up the server.
	 * 
	 * @param cleanTempDir
	 */
	void setCleanTempDir(boolean cleanTempDir);

	boolean isParentLoaderPriority();

	void setParentLoaderPriority(boolean parentLoaderPriority);
	
	/**
	 * @return <code>true</code> if server is setup to stop when a web application startup fails
	 */
	boolean isThrowIfStartupException();
	
	/**
	 * Set whether or not the server should stop when a web application startup fails
	 * 
	 * @param throwIfStartupException
	 */
	void setThrowIfStartupException(boolean throwIfStartupException);
	
    /**
     * @return the max period of inactivity, after which the session is invalidated, in seconds.
     */
    public int getMaxInactiveInterval();

    /**
     * Sets the max period of inactivity, after which the session is invalidated, in seconds.
     *
     * @param seconds the max inactivity period, in seconds.
     */
    public void setMaxInactiveInterval(int seconds);
}

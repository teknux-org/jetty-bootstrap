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

import org.eclipse.jetty.server.AbstractConnector;

import java.io.File;
import java.security.KeyStore;
import java.util.Set;


/**
 * This interface represents all the available configuration options for the jetty server
 */
public interface IJettyConfiguration extends Cloneable {

    IJettyConfiguration clone();

    /**
     * @return <code>true</code> to join jetty with main thread.
     */
    boolean isAutoJoinOnStart();

    /**
     * Set whether or not main thread should join jetty ones.
     * 
     * @param autoJoinOnStart
     *            boolean
     */
    void setAutoJoinOnStart(boolean autoJoinOnStart);

    /**
     * @return maximum number of threads jetty is allowed handle.
     */
    int getMaxThreads();

    /**
     * Set the maximum number of threads jetty is allowed handle.
     * 
     * @param maxThreads
     *            int
     */
    void setMaxThreads(int maxThreads);

    /**
     * Is jetty server must be stopped whenever the jettybootstrap stops. Otherwise jetty remains running.
     * 
     * @return <code>true</code> to stop jetty at application stop. <code>false</code> to keep jetty running after the application life.
     */
    boolean isStopAtShutdown();

    /**
     * Set whether or not the jetty server must be stopped whenever the main application stops. Otherwise jetty remains running.
     * 
     * @param stopAtShutdown
     *            boolean
     */
    void setStopAtShutdown(boolean stopAtShutdown);

    /**
     * @return the timeout before forcing jetty to stop.
     */
    long getStopTimeout();

    /**
     * Set the timeout before forcing jetty to stop.
     * 
     * @param stopTimeout
     *            long
     */
    void setStopTimeout(long stopTimeout);

    /**
     * Get the connectors max idle time for a connection. This applies to all connectors.
     * 
     * @see AbstractConnector#setIdleTimeout
     * @return the idle time.
     */
    long getIdleTimeout();

    /**
     * Set the connectors max idle time for a connection. This applies to all connectors.
     * 
     * @see AbstractConnector#setIdleTimeout
     * @param idleTimeout
     *            long
     */
    void setIdleTimeout(long idleTimeout);

    /**
     * Get the connectors max blocking time for a connection. This applies to all connectors.
     *
     * @see org.eclipse.jetty.server.HttpConfiguration#getBlockingTimeout
     * @return the blocking time.
     */
    long getBlockingTimeout();

    /**
     * Set the connectors max blocking time for a connection. This applies to all connectors.
     *
     * @see org.eclipse.jetty.server.HttpConfiguration#setBlockingTimeout
     * @param blockingTimeout
     *            long
     */
    void setBlockingTimeout(long blockingTimeout);

    /**
     * Get the server host IP/Name.
     * 
     * @return String
     */
    String getHost();

    /**
     * Set the server host IP/Name.
     * 
     * @param host
     *            String
     */
    void setHost(String host);

    /**
     * Get the HTTP port number.
     * 
     * @return int
     */
    int getPort();

    /**
     * Set the HTTP port number.
     * 
     * @param port
     *            int
     */
    void setPort(int port);

    /**
     * Get the port number for SSL (HTTPS).
     * 
     * @return int
     */
    int getSslPort();

    /**
     * Set the port number used for SSL (HTTPS).
     * 
     * @param sslPort
     *            int
     */
    void setSslPort(int sslPort);

    /**
     * Check whether or not the given {@link JettyConnector} has to be supported by the server.
     * 
     * @param jettyConnector
     *            JettyConnector
     * @return boolean
     */
    boolean hasJettyConnector(JettyConnector jettyConnector);

    /**
     * Get the collection of connectors the server as to use.
     * 
     * @return Set of JettyConnector
     */
    Set<JettyConnector> getJettyConnectors();

    /**
     * Set what connectors the server has to use.
     * 
     * @param jettyConnectors
     *            JettyConnector Array
     */
    void setJettyConnectors(JettyConnector... jettyConnectors);

    /**
     * @return <code>true</code> if all http requests have to be redirected to https. <code>false</code> otherwise.
     */
    boolean isRedirectWebAppsOnHttpsConnector();

    /**
     * Set whether or not the server must redirect http request to https.
     * 
     * @param redirectWebAppsOnHttpsConnector
     *            <code>true</code> to redirect all http request to https. <code>false</code> otherwise.
     */
    void setRedirectWebAppsOnHttpsConnector(boolean redirectWebAppsOnHttpsConnector);

    /**
     * Get the SSL private key format, used for HTTPS support.
     * 
     * @return sslPrivateKeyFormat
     */
    JettySslFileFormat getSslPrivateKeyFormat();

    /**
     * Set the SSL private key format, used for HTTPS support.
     * 
     * @param sslPrivateKeyFormat
     */
    void setSslPrivateKeyFormat(JettySslFileFormat sslPrivateKeyFormat);

    /**
     * Get the SSL private key path, used for HTTPS support.
     * 
     * @return sslPrivateKeyPath
     */
    String getSslPrivateKeyPath();

    /**
     * Set the SSL private key path, used for HTTPS support.
     * 
     * @param sslPrivateKeyPath
     */
    void setSslPrivateKeyPath(String sslPrivateKeyPath);

    /**
     * Get the SSL private key password, used for HTTPS support.
     * 
     * @return sslPrivateKeyPassword
     */
    String getSslPrivateKeyPassword();

    /**
     * Set the SSL private key password, used for HTTPS support.
     * 
     * @param sslPrivateKeyPassword
     */
    void setSslPrivateKeyPassword(String sslPrivateKeyPassword);

    /**
     * Get the SSL certificate format, used for HTTPS support.
     * 
     * @return sslCertificateFormat
     */
    JettySslFileFormat getSslCertificateFormat();

    /**
     * Set the SSL certificate format, used for HTTPS support.
     * 
     * @param sslCertificateFormat
     */
    void setSslCertificateFormat(JettySslFileFormat sslCertificateFormat);

    /**
     * Get the SSL certificate path, used for HTTPS support.
     * 
     * @return sslCertificatePath
     */
    String getSslCertificatePath();

    /**
     * Set the SSL certificate path, use for HTTPS support.
     * 
     * @param sslCertificatePath
     */
    void setSslCertificatePath(String sslCertificatePath);

    /**
     * Get the SSL certificate password, used for HTTPS support.
     * 
     * @return sslCertificatePassword
     */
    String getSslCertificatePassword();

    /**
     * Set the SSL certificate password, used for HTTPS support.
     * 
     * @param sslCertificatePassword
     */
    void setSslCertificatePassword(String sslCertificatePassword);

    /**
     * Get the SSL keystore, used for HTTPS support.
     * 
     * @return sslKeystore
     */
    KeyStore getSslKeyStore();

    /**
     * Set the SSL keystore, used for HTTPS support.
     * 
     * @param sslKeyStore
     *            KeyStore
     */
    void setSslKeyStore(KeyStore sslKeyStore);

    /**
     * Get the SSL keystore file path, used for HTTPS support.
     * 
     * @return the path of the SSL keystore file.
     */
    String getSslKeyStorePath();

    /**
     * Set the SSL keystore file path, used for HTTPS support.
     * 
     * @param sslKeyStorePath
     *            String
     */
    void setSslKeyStorePath(String sslKeyStorePath);

    /**
     * Get the SSL keystore domain name
     * 
     * @return sslKeyStoreDomainName
     */
    String getSslKeyStoreDomainName();

    /**
     * Set the SSL keystore domain name
     * 
     * @param sslKeyStoreDomainName
     */
    void setSslKeyStoreDomainName(String sslKeyStoreDomainName);

    /**
     * Get the SSL keystore alias
     * 
     * @return sslKeyStoreAlias
     */
    String getSslKeyStoreAlias();

    /**
     * Set the SSL keystore alias
     * 
     * @param sslKeyStoreAlias
     */
    void setSslKeyStoreAlias(String sslKeyStoreAlias);

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
     *            String
     */
    void setSslKeyStorePassword(String sslKeyStorePassword);

    /**
     * Get the SSL keystore algorithm
     * 
     * @return sslKeyStoreAlgorithm
     */
    String getSslKeyStoreAlgorithm();

    /**
     * Set the SSL keystore algorithm
     * 
     * @param sslKeyStoreAlgorithm
     */
    void setSslKeyStoreAlgorithm(String sslKeyStoreAlgorithm);

    /**
     * Get the SSL keystore signature algorithm
     * 
     * @return sslKeyStoreSignatureAlgorithm
     */
    String getSslKeyStoreSignatureAlgorithm();

    /**
     * Set the SSL keystore signature algorithm
     * 
     * @param sslKeyStoreSignatureAlgorithm
     */
    void setSslKeyStoreSignatureAlgorithm(String sslKeyStoreSignatureAlgorithm);

    /**
     * Get the SSL keystore RDN OU Value
     * 
     * @return sslKeyStoreRdnOuValue
     */
    String getSslKeyStoreRdnOuValue();

    /**
     * Set the SSL keystore RDN OU Value
     * 
     * @param sslKeyStoreRdnOuValue
     */
    void setSslKeyStoreRdnOuValue(String sslKeyStoreRdnOuValue);

    /**
     * Get the SSL keystore RDN O Value
     * 
     * @return sslKeyStoreRdnOValue
     */
    String getSslKeyStoreRdnOValue();

    /**
     * Set the SSL keystore RDN O Value
     * 
     * @param sslKeyStoreRdnOValue
     */
    void setSslKeyStoreRdnOValue(String sslKeyStoreRdnOValue);

    /**
     * Get the SSL keystore number of days before validity
     * 
     * @return sslKeyStoreDateNotBeforeNumberOfDays
     */
    int getSslKeyStoreDateNotBeforeNumberOfDays();

    /**
     * Set the ssl keystore number of days before validaty
     * 
     * @param sslKeyStoreDateNotBeforeNumberOfDays
     */
    void setSslKeyStoreDateNotBeforeNumberOfDays(int sslKeyStoreDateNotBeforeNumberOfDays);

    /**
     * Get the SSL keystore number of days after validity
     * 
     * @return sslKeyStoreDateNotAgterNumberOfDays
     */
    int getSslKeyStoreDateNotAfterNumberOfDays();

    /**
     * Set the ssl keystore number of days after validaty
     * 
     * @param sslKeyStoreDateNotAfterNumberOfDays
     */
    void setSslKeyStoreDateNotAfterNumberOfDays(int sslKeyStoreDateNotAfterNumberOfDays);

    /**
     * @return the temporary directory used to deploy applications.
     */
    File getTempDirectory();

    /**
     * Set the temporary directory used to deploy applications.
     * 
     * @param tempDirectory
     *            File
     */
    void setTempDirectory(File tempDirectory);

    /**
     * @return <code>true</code> in case the applications temporary directory contents must be preserved over server stop/start. <code>false</code> otherwise.
     */
    boolean isPersistAppTempDirectories();

    /**
     * Set whether or not the applications temporary directory must be preserved over restart.
     * 
     * @param persistTempDirectory
     *            boolean
     */
    void setPersistAppTempDirectories(boolean persistTempDirectory);

    /**
     * @return <code>true</code> in case the server has to clear the temp directory before starting.
     */
    boolean isCleanTempDir();

    /**
     * Set whether or not a clear of the temp directory is needed before starting up the server.
     * 
     * @param cleanTempDir
     *            boolean
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
     *            boolean
     */
    void setThrowIfStartupException(boolean throwIfStartupException);

    /**
     * @return the max period of inactivity, after which the session is invalidated, in seconds.
     */
    int getMaxInactiveInterval();

    /**
     * Sets the max period of inactivity, after which the session is invalidated, in seconds.
     *
     * @param seconds
     *            the max inactivity period, in seconds.
     */
    void setMaxInactiveInterval(int seconds);
}

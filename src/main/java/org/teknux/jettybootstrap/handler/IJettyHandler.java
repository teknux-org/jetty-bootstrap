package org.teknux.jettybootstrap.handler;

import org.eclipse.jetty.server.Handler;
import org.teknux.jettybootstrap.JettyBootstrapException;


public interface IJettyHandler {

	Handler getHandler() throws JettyBootstrapException;
}

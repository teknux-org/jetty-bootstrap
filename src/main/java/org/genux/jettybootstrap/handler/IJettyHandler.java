package org.genux.jettybootstrap.handler;

import org.eclipse.jetty.server.Handler;
import org.genux.jettybootstrap.JettyBootstrapException;


public interface IJettyHandler {

	Handler getHandler() throws JettyBootstrapException;
}

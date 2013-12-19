package org.genux.jettybootstrap.handler;

import org.eclipse.jetty.server.Handler;
import org.genux.jettybootstrap.JettyException;


public interface IJettyHandler {

	Handler getHandler() throws JettyException;
}

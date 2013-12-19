package org.genux.jettybootstrap.handler;

import org.eclipse.jetty.server.Handler;


public class JettyHandler implements
		IJettyHandler {

	private Handler handler = null;

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}

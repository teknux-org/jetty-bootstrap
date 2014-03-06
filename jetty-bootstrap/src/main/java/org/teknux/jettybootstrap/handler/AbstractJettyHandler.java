package org.teknux.jettybootstrap.handler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Handler;
import org.teknux.jettybootstrap.JettyBootstrapException;
import org.teknux.jettybootstrap.handler.listener.IJettyLifeCycleListener;
import org.teknux.jettybootstrap.handler.listener.JettyLifeCycleListenerUtil;


abstract public class AbstractJettyHandler implements
		IJettyHandler {

	private List<IJettyLifeCycleListener> iJettyLifeCycleListeners = new ArrayList<IJettyLifeCycleListener>();

	public void addJettyLifeCycleListener(IJettyLifeCycleListener iJettyLifeCycleListener) {
		this.iJettyLifeCycleListeners.add(iJettyLifeCycleListener);
	}

	public void removeJettyLifeCycleListener(IJettyLifeCycleListener iJettyLifeCycleListener) {
		this.iJettyLifeCycleListeners.remove(iJettyLifeCycleListener);
	}

	@Override
	public Handler getHandler() throws JettyBootstrapException {
		Handler handler = createHandler();

		if (!iJettyLifeCycleListeners.isEmpty()) {
			for (IJettyLifeCycleListener iJettyLifeCycleListener : iJettyLifeCycleListeners) {
				handler.addLifeCycleListener(JettyLifeCycleListenerUtil.getBindedListener(this, iJettyLifeCycleListener));
			}
		}

		return handler;
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0} [{1}]", getItemType(), getItemName());
	}

	abstract protected Handler createHandler() throws JettyBootstrapException;
}

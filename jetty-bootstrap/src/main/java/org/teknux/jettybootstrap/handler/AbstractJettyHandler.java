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

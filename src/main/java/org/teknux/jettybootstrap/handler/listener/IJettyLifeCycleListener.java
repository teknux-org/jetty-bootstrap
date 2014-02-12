package org.teknux.jettybootstrap.handler.listener;

import org.eclipse.jetty.util.component.LifeCycle;
import org.teknux.jettybootstrap.handler.IJettyHandler;


public interface IJettyLifeCycleListener {

	public void lifeCycleStarting(IJettyHandler iJettyHandler, LifeCycle event);

	public void lifeCycleStarted(IJettyHandler iJettyHandler, LifeCycle event);

	public void lifeCycleFailure(IJettyHandler iJettyHandler, LifeCycle event, Throwable cause);

	public void lifeCycleStopping(IJettyHandler iJettyHandler, LifeCycle event);

	public void lifeCycleStopped(IJettyHandler iJettyHandler, LifeCycle event);
}

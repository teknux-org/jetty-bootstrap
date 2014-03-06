package org.teknux.jettybootstrap.handler.listener;

import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teknux.jettybootstrap.handler.IJettyHandler;


public class JettyLifeCycleListenerUtil {

	private final static Logger logger = LoggerFactory.getLogger(JettyLifeCycleListenerUtil.class);

	private JettyLifeCycleListenerUtil() {
	}

	public static LifeCycle.Listener getBindedListener(final IJettyHandler iJettyHandler, final IJettyLifeCycleListener listener) {
		return new LifeCycle.Listener() {

			@Override
			public void lifeCycleStarting(LifeCycle event) {
				listener.lifeCycleStarting(iJettyHandler, event);
			}

			@Override
			public void lifeCycleStarted(LifeCycle event) {
				listener.lifeCycleStarted(iJettyHandler, event);
			}

			@Override
			public void lifeCycleFailure(LifeCycle event, Throwable cause) {
				listener.lifeCycleFailure(iJettyHandler, event, cause);
			}

			@Override
			public void lifeCycleStopping(LifeCycle event) {
				listener.lifeCycleStopping(iJettyHandler, event);
			}

			@Override
			public void lifeCycleStopped(LifeCycle event) {
				listener.lifeCycleStopped(iJettyHandler, event);
			}
		};
	}

	public static IJettyLifeCycleListener getDefaultJettyLifeCycleListener() {
		return new IJettyLifeCycleListener() {

			@Override
			public void lifeCycleStarting(IJettyHandler iJettyHandler, LifeCycle event) {
				logger.trace("Starting {}...", iJettyHandler.toString());
			}

			@Override
			public void lifeCycleStarted(IJettyHandler iJettyHandler, LifeCycle event) {
				logger.trace("{} Started", iJettyHandler.toString());
			}

			@Override
			public void lifeCycleFailure(IJettyHandler iJettyHandler, LifeCycle event, Throwable cause) {
				logger.error("Failure {}", iJettyHandler.toString(), cause);
			}

			@Override
			public void lifeCycleStopping(IJettyHandler iJettyHandler, LifeCycle event) {
				logger.trace("Stopping {}...", iJettyHandler.toString());
			}

			@Override
			public void lifeCycleStopped(IJettyHandler iJettyHandler, LifeCycle event) {
				logger.trace("{} Stopped", iJettyHandler.toString());
			}
		};
	}
}

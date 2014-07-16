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
package org.teknux.jettybootstrap.handler.listener;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.util.component.LifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JettyLifeCycleLogListener {

	private final static Logger logger = LoggerFactory.getLogger(JettyLifeCycleLogListener.class);

	public static LifeCycle.Listener getLogListener(final Handler handler) {
		return new LifeCycle.Listener() {

			@Override
			public void lifeCycleStarting(LifeCycle event) {
			    logger.trace("Starting {}...", handler.toString());
			}

			@Override
			public void lifeCycleStarted(LifeCycle event) {
			    logger.trace("{} Started", handler.toString());
			}

			@Override
			public void lifeCycleFailure(LifeCycle event, Throwable cause) {
			    logger.error("Failure {}", handler.toString(), cause);
			}

			@Override
			public void lifeCycleStopping(LifeCycle event) {
				logger.trace("Stopping {}...", handler.toString());
			}

			@Override
			public void lifeCycleStopped(LifeCycle event) {
				logger.trace("{} Stopped", handler.toString());
			}
		};
	}
}

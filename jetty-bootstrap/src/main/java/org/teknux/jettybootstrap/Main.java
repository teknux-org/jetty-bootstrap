package org.teknux.jettybootstrap;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {

	private final static Logger LOGGER = LoggerFactory.getLogger(Main.class);

	/**
	 * @param args
	 * @throws JettyBootstrapException
	 */
	public static void main(String[] args) throws JettyBootstrapException {
		if (args.length == 0) {
			LOGGER.debug("Start Self...");
			JettyBootstrap.startSelf();
		} else {
			JettyBootstrap jettyBootstrap = new JettyBootstrap();

			for (String arg : args) {
				File file = new File(arg);

				if (!file.exists()) {
					LOGGER.warn("[{}] don't exists. Ignore application", file);
				} else {
					String contextPath = "/";

					if (file.isFile() && file.getName().toLowerCase().endsWith(".war")) {
						contextPath += file.getName().substring(0, file.getName().length() - 4);
					} else {
						contextPath += file.getName();
					}

					if (contextPath.equals("ROOT")) {
						contextPath = "/";
					}

					if (file.isDirectory()) {
						LOGGER.debug("[{}] exists and is directory. Add Exploded War Application...", file);
						jettyBootstrap.addExplodedWarApp(file.getPath(), null, contextPath);
					} else {
						if (file.isFile() && file.getName().toLowerCase().endsWith(".war")) {
							LOGGER.debug("[{}] exists and is war file. Add War Application...", file);
							jettyBootstrap.addWarApp(file.getPath(), contextPath);
						} else {
							LOGGER.warn("[{}] exists but is unknown file. Ignore application", file);
						}
					}
				}
			}
			jettyBootstrap.startServer();
		}
	}
}

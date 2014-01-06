package org.genux.jettybootstrap.test.handler;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.server.Handler;
import org.genux.jettybootstrap.JettyBootstrapException;
import org.genux.jettybootstrap.handler.WebAppWarJettyHandler;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WebAppWarJettyHandlerTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void do01WebAppWarJettyHandlerTest() throws JettyBootstrapException, IOException {

		File tempDir = temporaryFolder.newFolder();
		WebAppWarJettyHandler webAppWarJettyHandler = new WebAppWarJettyHandler();
		webAppWarJettyHandler.setContextPath("myContext");
		webAppWarJettyHandler.setTempDirectory(tempDir);
		webAppWarJettyHandler.setWarFile("/tmp/myWarFile.war");
		webAppWarJettyHandler.setPersistTempDirectory(false);
		webAppWarJettyHandler.setParentLoaderPriority(true);

		Handler handler = webAppWarJettyHandler.getHandler();

		Assert.assertEquals("WebAppContext", handler.getClass().getSimpleName());
	}
}

package com.dianping.phoenix.bootstrap.jboss4;

import java.net.URL;

import org.junit.Test;

import com.dianping.phoenix.spi.internal.DevModeWebappProvider;

public class Jboss4DevServer extends AbstractJboss4Bootstrap {
	@Override
	protected String getWebXml() {
		URL webXmlResource = getClass().getResource("/WEB-INF/web.xml");

		if (webXmlResource != null) {
			return webXmlResource.getPath();
		}

		return super.getWebXml();
	}

	@Test
	public void startServer() throws Exception {
		DevModeWebappProvider kernelProvider = new DevModeWebappProvider("../phoenix-kernel", "phoenix-kernel");
		DevModeWebappProvider appProvider = new DevModeWebappProvider("../phoenix-samples/sample-app1", "sample-app1");

		startJboss(kernelProvider, appProvider);
		display("/");

		System.out.println("Press any key to stop the server ...");
		System.in.read();
	}
}

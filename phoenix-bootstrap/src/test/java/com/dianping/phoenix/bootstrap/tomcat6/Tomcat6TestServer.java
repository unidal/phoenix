package com.dianping.phoenix.bootstrap.tomcat6;

import java.net.URL;

import org.junit.Test;

import com.dianping.phoenix.spi.internal.DevModeWebappProvider;

public class Tomcat6TestServer extends AbstractTomcat6Bootstrap {
	@Override
	protected String getWebXml() {
		URL webXml = getClass().getResource("web.xml");

		if (webXml != null) {
			return webXml.getPath();
		}

		return super.getWebXml();
	}

	@Test
	public void startServer() throws Exception {
		DevModeWebappProvider kernelProvider = new DevModeWebappProvider("../phoenix-kernel", "phoenix-kernel");
		DevModeWebappProvider appProvider = new DevModeWebappProvider("../phoenix-samples/sample-app1", "sample-app1");

		startTomcat(kernelProvider, appProvider);
		display("/console");

		System.out.println("Press any key to stop the server ...");
		System.in.read();
	}
}

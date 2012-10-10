package com.dianping.phoenix.bootstrap;

import org.junit.Test;

import com.dianping.phoenix.bootstrap.server.AbstractTomcat6Bootstrap;
import com.dianping.phoenix.bootstrap.server.DevModeWebappProvider;

public class Tomcat6DevServer extends AbstractTomcat6Bootstrap {
	@Test
	public void startServer() throws Exception {
		DevModeWebappProvider kernelProvider = new DevModeWebappProvider("../phoenix-kernel", "phoenix-kernel");
		DevModeWebappProvider appProvider = new DevModeWebappProvider("../phoenix-samples/sample-app1", "sample-app1");

		startTomcat(kernelProvider, appProvider);
		display("/");

		System.out.println("Press any key to stop the server ...");
		System.in.read();
	}
}

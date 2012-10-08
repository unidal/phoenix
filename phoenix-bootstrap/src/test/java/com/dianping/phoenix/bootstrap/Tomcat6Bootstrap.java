package com.dianping.phoenix.bootstrap;

import java.io.IOException;

import org.junit.Test;

import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader.WebappProvider;
import com.dianping.phoenix.bootstrap.server.AbstractTomcat6Bootstrap;

public class Tomcat6Bootstrap extends AbstractTomcat6Bootstrap {
	@Override
	protected WebappProvider getWebappProvider() throws IOException {
		return new MavenWebappProvider("../phoenix-samples/sample-app1", "sample-app1");
	}

	@Test
	public void startServer() throws Exception {
		startTomcat();
		display("/");
		
		System.out.println("Press any key to stop the server ...");
		System.in.read();
	}
}

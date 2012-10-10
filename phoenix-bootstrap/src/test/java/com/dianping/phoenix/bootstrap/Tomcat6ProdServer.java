package com.dianping.phoenix.bootstrap;

import org.junit.Test;

import com.dianping.phoenix.bootstrap.server.AbstractTomcat6Bootstrap;

public class Tomcat6ProdServer extends AbstractTomcat6Bootstrap {
	@Test
	public void startServer() throws Exception {
		startTomcat("../phoenix-kernel/target/phoenix-kernel", "../phoenix-samples/sample-app1/target/sample-app1");
		display("/");

		System.out.println("Press any key to stop the server ...");
		System.in.read();
	}
}

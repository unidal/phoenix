package com.dianping.phoenix.bootstrap.tomcat6;

import org.junit.Test;

public class Tomcat6ProdServer extends AbstractTomcat6Bootstrap {
	@Test
	public void startServer() throws Exception {
		startTomcat("../phoenix-kernel/target/phoenix-kernel", "../phoenix-samples/sample-app1/target/sample-app1");
		display("/inspect");

		System.out.println("Press any key to stop the server ...");
		System.in.read();
	}
}

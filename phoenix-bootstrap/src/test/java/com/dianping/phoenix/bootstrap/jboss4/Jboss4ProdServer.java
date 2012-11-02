package com.dianping.phoenix.bootstrap.jboss4;

import org.junit.Test;

public class Jboss4ProdServer extends AbstractJboss4Bootstrap {
	@Test
	public void startServer() throws Exception {
		startJboss("../phoenix-kernel/target/phoenix-kernel", "../phoenix-samples/sample-app1/target/sample-app1");
		display("/inspect");

		System.out.println("Press any key to stop the server ...");
		System.in.read();
	}
}

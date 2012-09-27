package com.dianping.phoenix.bootstrap;

import org.junit.Test;

import com.dianping.phoenix.bootstrap.server.AbstractTomcat6Bootstrap;

public class Tomcat6Bootstrap extends AbstractTomcat6Bootstrap {
	@Override
	protected String getBaseDir() {
		return "../phoenix-samples/sample-app1";
	}

	@Test
	public void startWebapp() throws Exception {
		startTomcat();
	}
}

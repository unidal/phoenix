package com.dianping.phoenix.bootstrap.tomcat6;

import java.io.File;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dianping.phoenix.spi.internal.DevModeWebappProvider;

public class Tomcat6DevServer extends AbstractTomcat6Bootstrap {
	
	@Before
	public void init(){
		URL webxml = Tomcat6DevServer.class.getClassLoader().getResource("WEB-INF/web.xml");
		if(webxml != null){
			System.setProperty(com.dianping.phoenix.bootstrap.Constants.WEB_XML_PATH_KEY, webxml.getPath());
		}
	}
	@After
	public void destory(){
		System.setProperty(com.dianping.phoenix.bootstrap.Constants.WEB_XML_PATH_KEY, null);
	}
	
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

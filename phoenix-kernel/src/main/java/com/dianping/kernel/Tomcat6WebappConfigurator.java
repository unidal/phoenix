package com.dianping.kernel;

import java.io.File;

import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader;
import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader.Configurator;

/**
 * @author bin.miao
 * 
 */
public class Tomcat6WebappConfigurator implements Configurator {
	private static final String webXmlPath = "WEB-INF/web.xml";

	private Tomcat6WebappRegister register;

	@Override
	public void configure(Tomcat6WebappLoader loader) {
		System.out.println("configure");
		System.out.println(String.format("Kernel war root: %s", loader.getKernelWarRoot()));
		System.out.println(String.format("War root: %s", loader.getWarRoot()));
		System.out.println(String.format("ServletContext: %s", loader.getServletContext()));
	}

	@Override
	public void postConfigure(Tomcat6WebappLoader loader) {
		// Init web.xml File
		File warRoot = loader.getWarRoot();
		File webXml = new File(warRoot, webXmlPath);

		this.register = new Tomcat6WebappRegister();

		try {
			this.register.start(loader, webXml);
		} catch (Exception e) {
			throw new RuntimeException("Tomcat6WebappConfigurator postConfigure error\r\n" + e.getMessage(), e);
		}
	}
}

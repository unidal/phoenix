package com.dianping.kernel;

import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader;
import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader.Configurator;

public class Tomcat6WebappConfigurator implements Configurator {
	@Override
	public void configure(Tomcat6WebappLoader loader) {
		System.out.println(String.format("Kernel war root: %s", loader.getKernelWarRoot()));
		System.out.println(String.format("War root: %s", loader.getWarRoot()));
		System.out.println(String.format("ServletContext: %s", loader.getServletContext()));
		new Tomcat6WebappRegistry().init(loader);
	}

}

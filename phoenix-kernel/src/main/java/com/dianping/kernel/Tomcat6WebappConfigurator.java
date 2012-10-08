package com.dianping.kernel;

import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader;
import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader.Configurator;

public class Tomcat6WebappConfigurator implements Configurator {
	@Override
	public void configure(Tomcat6WebappLoader loader) {
		System.out.println(String.format("Kernel war root: %s", loader.getKernelWarRoot()));
		System.out.println(String.format("War root: %s", loader.getWarRoot()));
		System.out.println(String.format("ServletContext: %s", loader.getServletContext()));
	}

	@Override
	public void postConfigure(Tomcat6WebappLoader loader) {
		try {
			new Tomcat6WebappRegistry().register(loader);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error when registering %s/web.xml!", loader.getWarRoot()), e);
		}
	}
}

package com.dianping.kernel;

import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader;
import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader.Configurator;

public class Tomcat6WebappConfigurator implements Configurator {
	@Override
	public void configure(Tomcat6WebappLoader loader) {
		System.out.println(loader.getClasspath());
		System.out.println("pre start");
	}

	@Override
	public void postConfigure(Tomcat6WebappLoader loader) {
		System.out.println("post start");
	}
}

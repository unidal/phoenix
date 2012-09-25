package com.dianping.phoenix.bootstrap;

import java.util.ServiceLoader;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.loader.WebappLoader;

public class Tomcat6WebappLoader extends WebappLoader {
	public Tomcat6WebappLoader() {
	}

	public Tomcat6WebappLoader(ClassLoader classloader) {
		super(classloader);
	}

	@Override
	public void start() throws LifecycleException {
		Configurator configurator = loadConfigurator(null);

		configurator.preStart(this);
		super.start();
		configurator.postStart(this);
	}

	private Configurator loadConfigurator(ClassLoader classloader) {
		if (classloader == null) {
			classloader = Thread.currentThread().getContextClassLoader();
		}

		ServiceLoader<Configurator> serviceLoader = ServiceLoader.load(Configurator.class, classloader);

		for (Configurator e : serviceLoader) {
			return e;
		}

		throw new UnsupportedOperationException("No implemented class found for " + Configurator.class);
	}

	public static interface Configurator {
		public void preStart(Tomcat6WebappLoader loader);

		public void postStart(Tomcat6WebappLoader loader);
	}
}

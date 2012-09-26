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

		configurator.configure(this);
		super.start();
		configurator.postConfigure(this);
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
		/**
		 * Configure the tomcat6 webapp loader before it starts.
		 * 
		 * @param loader
		 */
		public void configure(Tomcat6WebappLoader loader);

		/**
		 * Configure the tomcat6 webapp loader after it starts.
		 * 
		 * @param loader
		 */
		public void postConfigure(Tomcat6WebappLoader loader);
	}
}

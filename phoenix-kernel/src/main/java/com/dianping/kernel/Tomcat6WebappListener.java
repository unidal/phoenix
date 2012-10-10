package com.dianping.kernel;

import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader;
import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader.Listener;

public class Tomcat6WebappListener implements Listener {
	@Override
	public void initializing(Tomcat6WebappLoader loader) {
		System.out.println(String.format("Kernel war root: %s", loader.getKernelWarRoot()));
		System.out.println(String.format("War root: %s", loader.getWarRoot()));
		System.out.println(String.format("ServletContext: %s", loader.getServletContext()));
		new Tomcat6WebappRegistry().init(loader);
	}

	@Override
	public void beforeStarting(Tomcat6WebappLoader loader) {

	}

	@Override
	public void starting(Tomcat6WebappLoader loader) {
	}

	@Override
	public void afterStarted(Tomcat6WebappLoader loader) {
		try {
			new Tomcat6WebappRegistry().register();
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error when registering %s/web.xml!", loader.getWarRoot()), e);
		}
	}

	@Override
	public void stopping(Tomcat6WebappLoader loader) {

	}

	@Override
	public void destroying(Tomcat6WebappLoader loader) {

	}
}

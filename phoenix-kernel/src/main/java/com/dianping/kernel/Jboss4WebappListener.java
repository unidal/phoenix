package com.dianping.kernel;

import com.dianping.phoenix.bootstrap.Jboss4WebappLoader;
import com.dianping.phoenix.bootstrap.Jboss4WebappLoader.Listener;

public class Jboss4WebappListener implements Listener {
	private Jboss4WebappRegistry jboss4WebappRegistry;

	@Override
	public void initializing(Jboss4WebappLoader loader) {
		System.out.println(String.format("Web xml: %s", loader.getWebXml()));
		System.out.println(String.format("Kernel war root: %s", loader.getKernelWarRoot()));
		System.out.println(String.format("War root: %s", loader.getWarRoot()));
	}

	@Override
	public void beforeStarting(Jboss4WebappLoader loader) {

	}

	@Override
	public void starting(Jboss4WebappLoader loader) {
		this.jboss4WebappRegistry = new Jboss4WebappRegistry();
		this.jboss4WebappRegistry.init(loader);

		try {
			this.jboss4WebappRegistry.registerWebXml();
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error when registering %s/web.xml!", loader.getWarRoot()), e);
		}
	}

	@Override
	public void afterStarted(Jboss4WebappLoader loader) {
		this.jboss4WebappRegistry.reorderWebappElements();
	}

	@Override
	public void stopping(Jboss4WebappLoader loader) {
	}

	@Override
	public void destroying(Jboss4WebappLoader loader) {
	}
}

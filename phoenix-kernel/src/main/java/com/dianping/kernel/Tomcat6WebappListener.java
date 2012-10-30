package com.dianping.kernel;

import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader;
import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader.Listener;

public class Tomcat6WebappListener implements Listener {
	private CatalinaWebappPatcher m_patcher;

	@Override
	public void afterStarted(Tomcat6WebappLoader loader) {
		m_patcher.sortWebXmlElements();
	}

	@Override
	public void beforeStarting(Tomcat6WebappLoader loader) {
	}

	@Override
	public void destroying(Tomcat6WebappLoader loader) {
	}

	@Override
	public void initializing(Tomcat6WebappLoader loader) {
		System.out.println(String.format("Web xml: %s", loader.getWebXml()));
		System.out.println(String.format("Kernel war root: %s", loader.getKernelWarRoot()));
		System.out.println(String.format("War root: %s", loader.getWarRoot()));
	}

	@Override
	public void starting(Tomcat6WebappLoader loader) {
		m_patcher = new CatalinaWebappPatcher();
		m_patcher.init(loader);

		try {
			m_patcher.applyKernelWebXml();
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error when registering %s/web.xml!", loader.getWarRoot()), e);
		}
		try {
			m_patcher.mergeWebResources();
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error when registering %s/resources!", loader.getWarRoot()), e);
		}
	}

	@Override
	public void stopping(Tomcat6WebappLoader loader) {
	}
}

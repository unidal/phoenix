package com.dianping.kernel.plugin;

import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader;
import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader.Listener;

public class Tomcat6WebappListener implements Listener {
	private CatalinaWebappPatcher m_patcher;

	@Override
	public void afterStarted(Tomcat6WebappLoader loader) {
		m_patcher.finish();
		m_patcher.sortWebXmlElements();
		m_patcher.filterWebXmlElements();
	}

	@Override
	public void beforeStarting(Tomcat6WebappLoader loader) {
		m_patcher = new CatalinaWebappPatcher();
		
		try{
			m_patcher.init(loader);
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error when patching %s initCatalinaWebappPatcher!", loader.getWarRoot()), e);
		}
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
		try {
			m_patcher.applyKernelWebXml();
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error when patching %s/web.xml!", loader.getWarRoot()), e);
		}
		try {
			m_patcher.mergeWebResources();
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error when patching %s/resources!", loader.getWarRoot()), e);
		}
	}

	@Override
	public void stopping(Tomcat6WebappLoader loader) {
	}
}

package com.dianping.kernel;

import com.dianping.phoenix.bootstrap.Jboss4WebappLoader;
import com.dianping.phoenix.bootstrap.Jboss4WebappLoader.Listener;

public class Jboss4WebappListener implements Listener {
	private CatalinaWebappPatcher m_patcher;

	@Override
	public void afterStarted(Jboss4WebappLoader loader) {
		m_patcher.release();
		m_patcher.sortWebXmlElements();
	}

	@Override
	public void beforeStarting(Jboss4WebappLoader loader) {
		m_patcher = new CatalinaWebappPatcher();
		
		try{
			m_patcher.init(loader);;
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error when patching %s initCatalinaWebappPatcher!", loader.getWarRoot()), e);
		}
	}

	@Override
	public void destroying(Jboss4WebappLoader loader) {
	}

	@Override
	public void initializing(Jboss4WebappLoader loader) {
		System.out.println(String.format("Web xml: %s", loader.getWebXml()));
		System.out.println(String.format("Kernel war root: %s", loader.getKernelWarRoot()));
		System.out.println(String.format("War root: %s", loader.getWarRoot()));
	}

	@Override
	public void starting(Jboss4WebappLoader loader) {
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
	public void stopping(Jboss4WebappLoader loader) {
	}
}

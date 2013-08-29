package com.dianping.phoenix.dev.core.tools.generator.dynamic;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.dianping.phoenix.dev.core.configure.Whiteboard;
import com.dianping.phoenix.dev.core.configure.WorkspaceInitializedListener;
import com.dianping.phoenix.dev.core.tools.wms.ResourceUtil;
import com.dianping.phoenix.dev.core.tools.wms.WorkspaceConstants;

public class DefaultF5Manager implements F5Manager, Initializable, WorkspaceInitializedListener {
	
	private final static String CONFIG_FILE = "f5-pool.properties";
	
	private static Logger log = Logger.getLogger(DefaultF5Manager.class);
	
	private Properties projectName2PoolName = new Properties();

	@Override
	public F5Pool poolForProject(String projectName) {
		F5Pool pool = null;
		
		String poolName = projectName2PoolName.getProperty(projectName);
		if(poolName != null) {
			pool = new F5Pool();
			pool.setProjectName(projectName);
			pool.setPoolName(poolName);
			pool.setUrl(String.format("http://127.0.0.1:8080/_%s%%s", projectName));
		}
		
		return pool;
	}
	
	@Override
	public void initialize() throws InitializationException {
		Whiteboard.INSTANCE.addWorkspaceInitializedListener(this);
	}

	@Override
	public void onWorkspaceInitialized(File wsDir) {
		File file = new File(new File(wsDir, WorkspaceConstants.PHOENIX_CONFIG_FOLDER), CONFIG_FILE);
		try {
			projectName2PoolName.load(ResourceUtil.INSTANCE.loadFromFileOrClasspath(file));
		} catch (Exception e) {
			log.error(String.format("error read %s", CONFIG_FILE), e);
		}
	}

}

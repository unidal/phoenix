package com.dianping.phoenix.service;

import java.io.File;
import java.io.IOException;

import com.dianping.phoenix.configure.ConfigManager;

public class MockWarService implements WarService{
	
	private ConfigManager m_configManager;

	@Override
	public void downloadAndExtractTo(String version, File target)
			throws IOException {
		File workingDir = new File(m_configManager.getGitWorkingDir());
		File newFile = new File(workingDir,String.valueOf(System.currentTimeMillis()));
		newFile.createNewFile();
	}

	public ConfigManager getConfigManager() {
		return m_configManager;
	}

	public void setConfigManager(ConfigManager configManager) {
		this.m_configManager = configManager;
	}

}

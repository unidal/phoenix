package com.dianping.phoenix.service;

import java.io.File;
import java.io.IOException;

import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;

public class MockWarService implements WarService {
	@Inject
	private ConfigManager m_configManager;

	@Override
	public void downloadAndExtractTo(String version, File target) throws IOException {
		File workingDir = new File(m_configManager.getGitWorkingDir());
		File newFile = new File(workingDir, String.valueOf(System.currentTimeMillis()));

		newFile.createNewFile();
	}
}

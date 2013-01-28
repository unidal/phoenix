package com.dianping.phoenix.service;

import java.io.File;
import java.io.IOException;

import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;

public class MockWarService implements WarService {
	@Inject
	private ConfigManager m_configManager;

	@Override
	public void downloadAndExtractTo(String type, String version, File target) throws IOException {
		File workingDir = new File(m_configManager.getGitWorkingDir("phoenix-kernel"));
		File newFile = new File(workingDir, String.valueOf(System.currentTimeMillis()));

		newFile.createNewFile();
	}

	@Override
	public String getWarUrl(String type, String version) {
		throw new UnsupportedOperationException();
	}
}

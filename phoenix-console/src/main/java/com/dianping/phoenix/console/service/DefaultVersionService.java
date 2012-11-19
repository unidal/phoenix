package com.dianping.phoenix.console.service;

import java.io.File;

import org.unidal.lookup.annotation.Inject;

public class DefaultVersionService implements VersionService {
	@Inject
	private WarService m_warService;

	@Inject
	private GitService m_gitService;

	@Inject
	private VersionManager m_versionManager;

	@Override
	public void createVersion(String version, String description)
			throws Exception {
		File gitDir = m_gitService.getWorkingDir();
		m_gitService.pull();
		m_gitService.clearWorkingDir();
		m_warService.downloadAndExtractTo(version, gitDir);
		m_gitService.commit(version, description);
		m_gitService.push();
		m_versionManager.createVersion(version, description);
	}

	@Override
	public void removeVersion(String version) throws Exception {
		m_versionManager.removeVersion(version);
	}
}

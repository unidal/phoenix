package com.dianping.phoenix.service;

import java.io.File;
import java.io.FileNotFoundException;

import org.unidal.helper.Threads.Task;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.console.dal.deploy.VersionDao;

public class VersionExecutor implements Task{
	
	private boolean m_active;
	
	private StatusReporter m_reporter = null;

	private WarService m_warService = null;

	private GitService m_gitService = null;

	private VersionDao m_dao = null;
	
	private String version = null;
	
	private String description = null;
	
	public VersionExecutor(){
		
	}

	@Override
	public void run() {
try{
	m_gitService.setup();

	File gitDir = m_gitService.getWorkingDir();

	m_gitService.pull();
	m_gitService.clearWorkingDir();
	try {
		m_warService.downloadAndExtractTo(version, gitDir);
	} catch (FileNotFoundException fe) {
		m_reporter.log(String.format(
				"can not find war for version: %s ...", version));
		return;
	}

	m_gitService.commit(version, description);
	m_gitService.push();

	
}catch(Exception e){
	
}
	}
	
	

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public void shutdown() {
		m_active = false;
	}

}

package com.dianping.phoenix.service;


import org.unidal.helper.Threads.Task;

public class VersionExecutor implements Task{
	
	private boolean m_active;
	
	private String m_version;
	
	private String m_description;
	
	private VersionManager m_manager;

	private int m_versionId;
	
	public VersionExecutor(int versionId,String version, String description,VersionManager manager){
		m_version = version;
		m_description = description;
		m_manager = manager;
		m_versionId = versionId;
	}

	@Override
	public void run() {
		try{
			
		m_manager.submitVersion(m_version, m_description);
		
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		
		m_manager.updateVersionSuccessed(m_versionId);
		
		try {
			Thread.sleep(30*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		m_manager.clearVersion(m_version);
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

package com.dianping.phoenix.service;

import java.util.List;

import com.dianping.phoenix.console.dal.deploy.Version;

public interface VersionManager {
	public void createVersion(String version, String description, String releaseNotes, String createdBy)
	      throws Exception;

	public void removeVersion(String version) throws Exception;

	public List<Version> getActiveVersions() throws Exception;
	
	public void setWarService(WarService warService);
}

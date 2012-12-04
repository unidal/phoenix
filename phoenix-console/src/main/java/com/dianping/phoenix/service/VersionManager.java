package com.dianping.phoenix.service;

import java.util.List;

import com.dianping.phoenix.console.dal.deploy.Version;

public interface VersionManager {
	public Version createVersion(String version, String description,
			String releaseNotes, String createdBy) throws Exception;

	public void removeVersion(int id) throws Exception;

	public List<Version> getActiveVersions() throws Exception;

	public String getStatus();
}

package com.dianping.phoenix.service;

import java.util.List;

import org.unidal.dal.jdbc.DalException;

import com.dianping.phoenix.console.dal.deploy.Version;

public interface VersionManager {
	public Version createVersion(String version, String description,
			String releaseNotes, String createdBy) throws Exception;
	
	public void submitVersion(String version,String description) throws Exception;

	public void removeVersion(int id) throws Exception;

	List<Version> getFinishedVersions() throws Exception;

	Version getActiveVersion() throws Exception;

	void updateVersionSuccessed(int versionId);

	void clearVersion(String version);

	String getStatus(String version, int index);

	Version store(String version, String description, String releaseNotes,
			String createdBy) throws DalException;
}

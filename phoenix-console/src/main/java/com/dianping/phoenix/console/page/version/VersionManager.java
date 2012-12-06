package com.dianping.phoenix.console.page.version;

import java.util.List;

import org.unidal.dal.jdbc.DalException;

import com.dianping.phoenix.console.dal.deploy.Version;
import com.dianping.phoenix.console.page.version.DefaultVersionManager.VersionLog;
import com.dianping.phoenix.service.StatusReporter;

public interface VersionManager {
	public Version createVersion(String version, String description,
			String releaseNotes, String createdBy) throws Exception;

	void submitVersion(VersionContext context) throws VersionException;

	void removeVersion(int id) throws Exception;

	List<Version> getFinishedVersions() throws Exception;

	Version getActiveVersion() throws Exception;

	void updateVersionSuccessed(int versionId) throws DalException;

	void clearVersion(String version);

	VersionLog getStatus(String version, int index);

	Version store(String version, String description, String releaseNotes,
			String createdBy) throws DalException;

	public StatusReporter getReporter();
}

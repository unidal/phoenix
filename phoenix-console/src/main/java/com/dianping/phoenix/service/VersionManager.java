package com.dianping.phoenix.service;

public interface VersionManager {
	public void createVersion(String version, String description, String releaseNotes, String createdBy);

	public void removeVersion(String version);
}

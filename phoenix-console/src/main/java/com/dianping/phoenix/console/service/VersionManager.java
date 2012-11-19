package com.dianping.phoenix.console.service;

public interface VersionManager {
	public void createVersion(String version, String description);

	public void removeVersion(String version);
}

package com.dianping.phoenix.console.service;

public interface VersionService {
	public void createVersion(String version, String description) throws Exception;

	public void removeVersion(String version) throws Exception;
}

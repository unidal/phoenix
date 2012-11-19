package com.dianping.phoenix.service;

public interface VersionService {
	public void createVersion(String version, String description) throws Exception;

	public void removeVersion(String version) throws Exception;
}

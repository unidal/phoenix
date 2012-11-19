package com.dianping.phoenix.console.service;

import java.io.File;

public interface GitService {
	public void clearWorkingDir() throws Exception;

	public void commit(String tag, String description) throws Exception;

	public void pull() throws Exception;

	public void push() throws Exception;

	public File getWorkingDir();
}

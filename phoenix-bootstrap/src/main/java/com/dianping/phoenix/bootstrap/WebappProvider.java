package com.dianping.phoenix.bootstrap;

import java.io.File;
import java.util.List;

public interface WebappProvider {
	public List<File> getClasspathEntries();

	public File getWarRoot();
}
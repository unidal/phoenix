package com.dianping.phoenix.dev.core.tools.utils;

import java.io.File;
import java.io.FileFilter;

public class WebProjectFileFilter implements FileFilter {

	public boolean hasWebProjectIn(File dir) {
		return new File(dir, "src/main/webapp/WEB-INF/web.xml").exists() || new File(dir, "WEB-INF/web.xml").exists();
	}

	@Override
	public boolean accept(File pathname) {
		return pathname.isDirectory() && hasWebProjectIn(pathname);
	}
	
}

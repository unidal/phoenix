package com.dianping.phoenix.dev.core.tools.utils;

import java.io.File;

public class PomParser {

	public String getArtifactId(File dir) {
		return dir.getName();
	}
	
}

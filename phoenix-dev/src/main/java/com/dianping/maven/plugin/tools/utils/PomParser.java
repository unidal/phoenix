package com.dianping.maven.plugin.tools.utils;

import java.io.File;

public class PomParser {

	public String getArtifactId(File dir) {
		return dir.getName();
	}
	
}

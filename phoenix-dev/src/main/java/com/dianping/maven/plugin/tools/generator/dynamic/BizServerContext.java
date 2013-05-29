package com.dianping.maven.plugin.tools.generator.dynamic;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BizServerContext {

	private Map<String, File> ctxMap = new HashMap<String, File>();

	public Map<String, File> getCtxMap() {
		return ctxMap;
	}
	
	public void addWebContext(String ctxPath, File webRoot) {
		ctxMap.put(ctxPath, webRoot);
	}
	
}

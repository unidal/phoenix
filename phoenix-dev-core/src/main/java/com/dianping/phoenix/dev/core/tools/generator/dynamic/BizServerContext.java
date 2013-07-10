package com.dianping.phoenix.dev.core.tools.generator.dynamic;

import java.util.HashMap;
import java.util.Map;

public class BizServerContext {

	private Map<String, String> ctxMap = new HashMap<String, String>();

	public Map<String, String> getCtxMap() {
		return ctxMap;
	}
	
	public void addWebContext(String ctxPath, String webRoot) {
		ctxMap.put(ctxPath, webRoot);
	}
	
}

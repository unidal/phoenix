package com.dianping.maven.plugin.tools.generator.dynamic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dianping.maven.plugin.phoenix.model.entity.VirtualServer;

public class UrlRuleContext {

	private Map<String, VirtualServer> vsMap = new HashMap<String, VirtualServer>();
	private List<F5Pool> localPoolList = new ArrayList<F5Pool>();

	public String getDefaultUrlPattern(String virtualServer) {
		String defaultUrlPattern = null;
		if(vsMap.containsKey(virtualServer)) {
			defaultUrlPattern = vsMap.get(virtualServer).getDefaultUrlPattern();
		}
		return defaultUrlPattern;
	}
	
	public void addVirtualServer(VirtualServer vs) {
		vsMap.put(vs.getName(), vs);
	}
	
	public Collection<VirtualServer> getVirtualServerList() {
		return vsMap.values();
	}
	
	public VirtualServer getVirtualServer(String name) {
		return vsMap.get(name);
	}

	public void addLocalPool(F5Pool pool) {
		localPoolList.add(pool);
	}

	public List<F5Pool> getLocalPoolList() {
		return localPoolList;
	}

}

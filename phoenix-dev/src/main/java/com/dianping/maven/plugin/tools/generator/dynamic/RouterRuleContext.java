package com.dianping.maven.plugin.tools.generator.dynamic;

import java.util.ArrayList;
import java.util.List;

public class RouterRuleContext {

	private String defaultUrlPattern;
	private List<F5Pool> localPoolList = new ArrayList<F5Pool>();

	public String getDefaultUrlPattern() {
		return defaultUrlPattern;
	}

	public void setDefaultUrlPattern(String defaultUrlPattern) {
		this.defaultUrlPattern = defaultUrlPattern;
	}

	public void addLocalPool(F5Pool pool) {
		localPoolList.add(pool);
	}

	public List<F5Pool> getLocalPoolList() {
		return localPoolList;
	}

}

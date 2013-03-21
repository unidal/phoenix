package com.dianping.phoenix.router;

import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

public class RuleManager extends ContainerHolder {
	
	@Inject
	RuleProvider ruleProvider;
	
	public String trans(String url) {
		for (Rule rule : ruleProvider.rules()) {
			if (rule.match(url)) {
				return rule.trans(url);
			}
		}
		return url;
	}

	public boolean match(String url) {
		for (Rule rule : ruleProvider.rules()) {
			if (rule.match(url)) {
				return true;
			}
		}
		return false;
	}
	
}

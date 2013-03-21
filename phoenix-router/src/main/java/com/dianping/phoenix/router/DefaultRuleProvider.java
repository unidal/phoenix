package com.dianping.phoenix.router;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.router.model.entity.SerializedRule;

public class DefaultRuleProvider extends ContainerHolder implements RuleProvider, Initializable {

	@Inject
	private ConfigManager config;
	private List<Rule> rules = new ArrayList<Rule>();

	@Override
	public List<Rule> rules() {
		return rules;
	}

	@Override
	public void initialize() throws InitializationException {
		List<SerializedRule> serializedRules = config.getSerializedRules();

		for (SerializedRule sRule : serializedRules) {
			if ("regex".equals(sRule.getType())) {
				Rule rule = new RegexRule(sRule.getPatterns(), sRule.getTargetUrl());
				rules.add(rule);
			}
		}
	}

}

package com.dianping.phoenix.router;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

public class RouteServiceTestConfigurator extends AbstractResourceConfigurator {

	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(MockConfigManager.class));
		all.add(C(RuleProvider.class, DefaultRuleProvider.class).req(MockConfigManager.class));
		all.add(C(RuleManager.class).req(RuleProvider.class));
		all.add(C(RouteService.class).req(RuleManager.class));

		return all;
	}
	
	@Override
	protected Class<?> getTestClass() {
		return RouteServiceTest.class;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new RouteServiceTestConfigurator());
	}

}

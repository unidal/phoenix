package com.dianping.phoenix.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.router.DefaultRuleProvider;
import com.dianping.phoenix.router.RouteService;
import com.dianping.phoenix.router.RuleManager;
import com.dianping.phoenix.router.RuleProvider;

public class ComponentsConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(ConfigManager.class));
		all.add(C(RuleProvider.class, DefaultRuleProvider.class).req(ConfigManager.class));
		all.add(C(RuleManager.class).req(RuleProvider.class));
		all.add(C(RouteService.class).req(RuleManager.class));
		
		return all;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ComponentsConfigurator());
	}
}

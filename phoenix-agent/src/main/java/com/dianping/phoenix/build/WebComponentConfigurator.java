package com.dianping.phoenix.build;

import java.util.ArrayList;
import java.util.List;

import com.dianping.phoenix.agent.AgentModule;

import com.site.lookup.configuration.Component;
import com.site.web.configuration.AbstractWebComponentsConfigurator;

class WebComponentConfigurator extends AbstractWebComponentsConfigurator {
	@SuppressWarnings("unchecked")
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		defineModuleRegistry(all, AgentModule.class, AgentModule.class);

		return all;
	}
}

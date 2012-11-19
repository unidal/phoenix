package com.dianping.phoenix.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.Component;
import org.unidal.web.configuration.AbstractWebComponentsConfigurator;

import com.dianping.phoenix.console.ConsoleModule;

class WebComponentConfigurator extends AbstractWebComponentsConfigurator {
	@SuppressWarnings("unchecked")
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		defineModuleRegistry(all, ConsoleModule.class, ConsoleModule.class);

		return all;
	}
}

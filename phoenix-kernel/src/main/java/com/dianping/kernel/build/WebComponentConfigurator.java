package com.dianping.kernel.build;

import java.util.ArrayList;
import java.util.List;

import com.dianping.kernel.inspect.InspectModule;

import org.unidal.lookup.configuration.Component;
import org.unidal.web.configuration.AbstractWebComponentsConfigurator;

class WebComponentConfigurator extends AbstractWebComponentsConfigurator {
	@SuppressWarnings("unchecked")
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		defineModuleRegistry(all, InspectModule.class, InspectModule.class);

		return all;
	}
}

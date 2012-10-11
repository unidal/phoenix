package com.dianping.phoenix.build;

import java.util.ArrayList;
import java.util.List;

import com.dianping.phoenix.console.service.DeployService;
import com.dianping.phoenix.console.service.ProjectService;
import com.site.lookup.configuration.AbstractResourceConfigurator;
import com.site.lookup.configuration.Component;

public class ComponentsConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(ProjectService.class));
		all.add(C(DeployService.class));

		// Please keep it as last
		all.addAll(new WebComponentConfigurator().defineComponents());

		return all;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ComponentsConfigurator());
	}
}

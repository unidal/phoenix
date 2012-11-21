package com.dianping.phoenix.service;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.phoenix.configure.ConfigManager;

public class VersionManagerTestConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(WarService.class, MockWarService.class) //
		      .req(ConfigManager.class));

		return all;
	}

	@Override
	protected Class<?> getTestClass() {
		return VersionManagerTest.class;
	}
	
	public static void main(String[] args) {
	   generatePlexusComponentsXmlFile(new VersionManagerTestConfigurator());
   }
}

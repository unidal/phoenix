package com.dianping.service.mock;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.service.spi.ServiceProvider;

public class MockServiceTestConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(ServiceProvider.class, MockService.class.getName(), MockServiceProvider.class));

		return all;
	}

	@Override
	protected Class<?> getTestClass() {
		return MockServiceTest.class;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new MockServiceTestConfigurator());
	}
}

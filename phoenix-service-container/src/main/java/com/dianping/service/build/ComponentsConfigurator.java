package com.dianping.service.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.cat.Cat;
import com.dianping.service.ServiceContainer;
import com.dianping.service.editor.model.ModelBuilder;
import com.dianping.service.internal.DefaultCatProvider;
import com.dianping.service.logging.Log4jLoggerProvider;
import com.dianping.service.logging.PlexusLoggerProvider;
import com.dianping.service.spi.ServiceConfigurator;
import com.dianping.service.spi.ServiceManager;
import com.dianping.service.spi.ServiceProvider;
import com.dianping.service.spi.ServiceRegistry;
import com.dianping.service.spi.internal.DefaultServiceConfigurator;
import com.dianping.service.spi.internal.DefaultServiceManager;
import com.dianping.service.spi.internal.DefaultServiceRegistry;
import com.dianping.service.spi.lifecycle.DefaultServiceContext;
import com.dianping.service.spi.lifecycle.DefaultServiceLifecycle;
import com.dianping.service.spi.lifecycle.ServiceContext;
import com.dianping.service.spi.lifecycle.ServiceLifecycle;

public class ComponentsConfigurator extends AbstractResourceConfigurator {
	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ComponentsConfigurator());
	}

	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(ServiceContainer.class) //
		      .req(ServiceManager.class));
		all.add(C(ServiceManager.class, DefaultServiceManager.class) //
		      .req(ServiceRegistry.class, ServiceLifecycle.class));
		all.add(C(ServiceRegistry.class, DefaultServiceRegistry.class));
		all.add(C(ServiceLifecycle.class, DefaultServiceLifecycle.class) //
		      .req(ServiceConfigurator.class));
		all.add(C(ServiceConfigurator.class, DefaultServiceConfigurator.class));
		all.add(C(ServiceContext.class, DefaultServiceContext.class).is(PER_LOOKUP));

		all.addAll(defineFoundationServices());
		all.addAll(defineWebComponents());

		return all;
	}

	private List<Component> defineFoundationServices() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(ServiceProvider.class, org.codehaus.plexus.logging.Logger.class.getName(), PlexusLoggerProvider.class));
		all.add(C(ServiceProvider.class, org.apache.log4j.Logger.class.getName(), Log4jLoggerProvider.class));
		all.add(C(ServiceProvider.class, Cat.class.getName(), DefaultCatProvider.class));

		return all;
	}

	private List<Component> defineWebComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(ModelBuilder.class) //
		      .req(ServiceRegistry.class));
		all.addAll(new WebComponentConfigurator().defineComponents());

		return all;
	}
}

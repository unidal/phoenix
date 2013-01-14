package com.dianping.service.spi.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.ContainerHolder;
import org.unidal.tuple.Pair;

import com.dianping.service.deployment.entity.DeploymentModel;
import com.dianping.service.deployment.entity.PropertyModel;
import com.dianping.service.deployment.entity.RequirementModel;
import com.dianping.service.deployment.entity.ServiceModel;
import com.dianping.service.deployment.transform.DefaultSaxParser;
import com.dianping.service.mock.MockService;
import com.dianping.service.spi.ServiceBinding;
import com.dianping.service.spi.ServiceProvider;
import com.dianping.service.spi.ServiceRegistry;
import com.dianping.service.spi.lifecycle.ServiceLifecycle;

public class DefaultServiceRegistry extends ContainerHolder implements ServiceRegistry, Initializable {
	private DeploymentModel m_model;

	private List<Pair<Class<?>, String>> getComponents(ServiceModel service) {
		List<Pair<Class<?>, String>> components = new ArrayList<Pair<Class<?>, String>>();

		for (RequirementModel requirement : service.getRequirements()) {
			components.add(new Pair<Class<?>, String>(requirement.getType(), requirement.getAlias()));
		}

		return components;
	}

	public DeploymentModel getDeployment() {
		return m_model;
	}

	private Map<String, String> getProperties(ServiceModel service) {
		Map<String, String> properties = new LinkedHashMap<String, String>();

		for (PropertyModel property : service.getProperties()) {
			properties.put(property.getName(), property.getValue());
		}

		return properties;
	}

	@Override
	public ServiceBinding getServiceBinding(Class<?> serviceType, String alias) {
		String type = serviceType.getName();

		if (alias == null) {
			alias = "default";
		}

		for (ServiceModel activeService : m_model.getActiveServices()) {
			if (type.equals(activeService.getType()) && alias.equals(activeService.getAlias())) {
				return new DefaultServiceBinding(alias, getProperties(activeService), activeService.getConfiguration(),
				      getComponents(activeService));
			}
		}

		throw new IllegalStateException(String.format("No active service(%s) found for alias(%s)!", type, alias));
	}

	@Override
	public List<ServiceBinding> getServiceBindings(Class<?> serviceType) {
		List<ServiceBinding> bindings = new ArrayList<ServiceBinding>();

		for (ServiceModel activeService : m_model.getActiveServices()) {
			if (serviceType.equals(activeService.getType())) {
				DefaultServiceBinding binding = new DefaultServiceBinding(activeService.getAlias(),
				      getProperties(activeService), activeService.getConfiguration(), getComponents(activeService));

				bindings.add(binding);
			}
		}

		return bindings;
	}

	@Override
	public ServiceLifecycle getServiceLifecycle(Class<?> serviceType) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> ServiceProvider<T> getServiceProvider(Class<T> serviceType) {
		String name = serviceType.getName();

		try {
			ServiceProvider<T> provider = lookup(ServiceProvider.class, name);

			return provider;
		} catch (Exception e) {
			throw new IllegalStateException(String.format("Unable to find ServiceProvider(%s)!", name), e);
		}
	}

	@Override
	public void initialize() throws InitializationException {
		String resource = "/com/dianping/service/deployment.xml";

		try {
			DeploymentModel model = DefaultSaxParser.parse(getClass().getResourceAsStream(resource));

			m_model = model;
		} catch (Exception e) {
			String message = String.format("Error when loading service footprint from %s!", resource);

			throw new InitializationException(message, e);
		}
	}

	@Override
	public void setServiceBinding(Class<MockService> serviceType, String alias, ServiceBinding binding) {
		if (alias == null) {
			alias = "default";
		}

		ServiceModel service = null;

		for (ServiceModel activeService : m_model.getActiveServices()) {
			if (serviceType.equals(activeService.getType()) && alias.equals(activeService.getAlias())) {
				service = activeService;
				break;
			}
		}

		if (service == null) {
			service = new ServiceModel();
			service.setAlias(alias);
			service.setType(serviceType);
			m_model.addService(service);
		} else {
			service.getProperties().clear();
		}

		service.setConfiguration(binding.getConfiguration());

		for (Map.Entry<String, String> e : binding.getProperties().entrySet()) {
			PropertyModel property = new PropertyModel();

			property.setName(e.getKey()).setValue(e.getValue());
			service.addProperty(property);
		}
	}

	@Override
	public String toString() {
		return m_model.toString();
	}
}

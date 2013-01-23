package com.dianping.service.spi.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.ContainerHolder;

import com.dianping.service.deployment.entity.DeploymentModel;
import com.dianping.service.deployment.entity.InstanceModel;
import com.dianping.service.deployment.entity.PropertyModel;
import com.dianping.service.deployment.entity.ServiceModel;
import com.dianping.service.deployment.transform.DefaultSaxParser;
import com.dianping.service.spi.ServiceBinding;
import com.dianping.service.spi.ServiceProvider;
import com.dianping.service.spi.ServiceRegistry;
import com.dianping.service.spi.lifecycle.ServiceLifecycle;

public class DefaultServiceRegistry extends ContainerHolder implements ServiceRegistry, Initializable {
	private static final String DEFAULT_ID = "default";

	private DeploymentModel m_model;

	public DeploymentModel getDeployment() {
		return m_model;
	}

	@Override
	public ServiceBinding getServiceBinding(Class<?> serviceType, String id) {
		if (id == null) {
			id = DEFAULT_ID;
		}

		for (ServiceModel activeService : m_model.getActiveServices()) {
			if (serviceType.equals(activeService.getType())) {
				for (InstanceModel instance : activeService.getInstances()) {
					if (id.equals(instance.getId())) {
						DefaultServiceBinding binding = new DefaultServiceBinding(instance);

						return binding;
					}
				}
			}
		}

		throw new IllegalStateException(String.format("No active service(%s) found for alias(%s)!",
		      serviceType.getName(), id));
	}

	@Override
	public List<ServiceBinding> getServiceBindings(Class<?> serviceType) {
		List<ServiceBinding> bindings = new ArrayList<ServiceBinding>();

		for (ServiceModel activeService : m_model.getActiveServices()) {
			if (serviceType.equals(activeService.getType())) {
				for (InstanceModel instance : activeService.getInstances()) {
					DefaultServiceBinding binding = new DefaultServiceBinding(instance);

					bindings.add(binding);
				}

				break;
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
	public boolean hasServiceBinding(Class<?> serviceType, String id) {
		if (id == null) {
			id = DEFAULT_ID;
		}

		for (ServiceModel activeService : m_model.getActiveServices()) {
			if (serviceType.equals(activeService.getType())) {
				for (InstanceModel instance : activeService.getInstances()) {
					if (id.equals(instance.getId())) {
						return true;
					}
				}
			}
		}

		return false;
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
	public void setServiceBinding(Class<?> serviceType, String id, ServiceBinding binding) {
		if (id == null) {
			id = DEFAULT_ID;
		}

		ServiceModel service = m_model.findOrCreateService(serviceType);
		InstanceModel instance = service.findOrCreateInstance(id);

		instance.getProperties().clear();
		instance.setConfiguration(binding.getConfiguration());

		for (Map.Entry<String, String> e : binding.getProperties().entrySet()) {
			PropertyModel property = new PropertyModel();

			property.setName(e.getKey()).setValue(e.getValue());
			instance.addProperty(property);
		}

		// TODO add requirements
	}

	@Override
	public String toString() {
		return m_model.toString();
	}
}

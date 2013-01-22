package com.dianping.service.editor.model;

import java.util.Map;

import org.unidal.lookup.annotation.Inject;

import com.dianping.service.deployment.entity.DeploymentModel;
import com.dianping.service.deployment.entity.InstanceModel;
import com.dianping.service.spi.ServiceBinding;
import com.dianping.service.spi.ServiceRegistry;
import com.dianping.service.spi.internal.DefaultServiceBinding;

public class ServiceAccessor {
	@Inject
	private ServiceRegistry m_registry;

	@Inject
	private DeploymentBuilder m_builder;

	public DeploymentModel buildDeployment() {
		DeploymentModel deployment = new DeploymentModel();

		deployment.accept(m_builder);
		return deployment;
	}

	public void updateProperties(String serviceType, String id, Map<String, String> properties) throws Exception {
		Class<?> type = Class.forName(serviceType);
		ServiceBinding binding = null;

		if (m_registry.hasServiceBinding(type, id)) {
			binding = m_registry.getServiceBinding(type, id);
		}

		if (binding == null) {
			InstanceModel instance = new InstanceModel(id);

			binding = new DefaultServiceBinding(instance);
		}

		binding.getProperties().putAll(properties);
		m_registry.setServiceBinding(type, id, binding);
	}
}

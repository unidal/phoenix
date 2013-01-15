package com.dianping.service.editor.model;

import java.util.Map;

import org.unidal.lookup.annotation.Inject;

import com.dianping.service.deployment.entity.DeploymentModel;
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

	public void updateProperties(String serviceType, String alias, Map<String, String> properties) throws Exception {
		Class<?> type = Class.forName(serviceType);
		ServiceBinding binding = null;

		if (m_registry.hasServiceBinding(type, alias)) {
			binding = m_registry.getServiceBinding(type, alias);
		}

		if (binding == null) {
			binding = new DefaultServiceBinding(alias, null);
		}

		binding.getProperties().putAll(properties);
		m_registry.setServiceBinding(type, alias, binding);
	}
}

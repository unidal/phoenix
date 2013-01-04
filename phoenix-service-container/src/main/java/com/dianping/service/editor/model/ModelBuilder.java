package com.dianping.service.editor.model;

import java.util.List;

import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

import com.dianping.service.deployment.IVisitor;
import com.dianping.service.deployment.entity.DeploymentModel;
import com.dianping.service.deployment.entity.PropertyModel;
import com.dianping.service.deployment.entity.RequirementModel;
import com.dianping.service.deployment.entity.ServiceModel;
import com.dianping.service.spi.ServiceBinding;
import com.dianping.service.spi.ServiceProvider;
import com.dianping.service.spi.ServiceRegistry;

public class ModelBuilder extends ContainerHolder implements IVisitor {
	@Inject
	private ServiceRegistry m_registry;

	@Override
	@SuppressWarnings("rawtypes")
	public void visitDeployment(DeploymentModel deployment) {
		List<ServiceProvider> providers = lookupList(ServiceProvider.class);

		for (ServiceProvider<?> provider : providers) {
			List<ServiceBinding> bindings = m_registry.getServiceBindings(provider.getServiceType());

			for (ServiceBinding binding : bindings) {
				ServiceModel service = new ServiceModel();

				service.setAlias(binding.getAlias());
				service.setConfiguration(binding.getConfiguration());
				service.setType(provider.getServiceType().toString());

				deployment.addService(service);
				visitService(service);
			}
		}
	}

	@Override
	public void visitProperty(PropertyModel property) {
	}

	@Override
	public void visitRequirement(RequirementModel requirement) {
	}

	@Override
	public void visitService(ServiceModel service) {
	}
}

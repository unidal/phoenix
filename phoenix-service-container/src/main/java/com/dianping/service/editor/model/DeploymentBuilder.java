package com.dianping.service.editor.model;

import java.util.List;
import java.util.Map;

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

public class DeploymentBuilder extends ContainerHolder implements IVisitor {
	@Inject
	private ServiceRegistry m_registry;

	@Override
	@SuppressWarnings("rawtypes")
	public void visitDeployment(DeploymentModel deployment) {
		List<ServiceProvider> providers = lookupList(ServiceProvider.class);

		for (ServiceProvider<?> provider : providers) {
			List<ServiceBinding> bindings = m_registry.getServiceBindings(provider.getServiceType());

			if (bindings.isEmpty()) {
				ServiceModel service = new ServiceModel();
				
				service.setType(provider.getServiceType());
				deployment.addService(service);
			} else {
				for (ServiceBinding binding : bindings) {
					ServiceModel service = new ServiceModel();
					List<PropertyModel> properties = service.getProperties();

					service.setAlias(binding.getAlias());
					service.setConfiguration(binding.getConfiguration());
					service.setType(provider.getServiceType());

					for (Map.Entry<String, String> e : binding.getProperties().entrySet()) {
						properties.add(new PropertyModel().setName(e.getKey()).setValue(e.getValue()));
					}

					deployment.addService(service);
				}
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

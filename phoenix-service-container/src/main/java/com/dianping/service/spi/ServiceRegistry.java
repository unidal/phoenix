package com.dianping.service.spi;

import java.util.List;

import com.dianping.service.mock.MockService;
import com.dianping.service.spi.lifecycle.ServiceLifecycle;

public interface ServiceRegistry {
	public List<ServiceBinding> getServiceBindings(Class<?> serviceType);

	public ServiceBinding getServiceBinding(Class<?> serviceType, String alias);

	public ServiceLifecycle getServiceLifecycle(Class<?> serviceType);

	public <T> ServiceProvider<T> getServiceProvider(Class<T> serviceType);

	public void setServiceBinding(Class<MockService> serviceType, String alias, ServiceBinding binding);
}

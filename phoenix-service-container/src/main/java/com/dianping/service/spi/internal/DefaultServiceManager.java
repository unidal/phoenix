package com.dianping.service.spi.internal;

import java.util.HashMap;
import java.util.Map;

import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

import com.dianping.service.spi.ServiceBinding;
import com.dianping.service.spi.ServiceManager;
import com.dianping.service.spi.ServiceProvider;
import com.dianping.service.spi.ServiceRegistry;
import com.dianping.service.spi.lifecycle.ServiceContext;
import com.dianping.service.spi.lifecycle.ServiceLifecycle;

public class DefaultServiceManager extends ContainerHolder implements ServiceManager {
	@Inject
	private ServiceRegistry m_registry;

	@Inject
	private ServiceLifecycle m_lifecycle;

	private Map<Class<?>, Map<String, Object>> m_map = new HashMap<Class<?>, Map<String, Object>>();

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getService(Class<T> serviceType, String alias) throws Exception {
		Map<String, Object> map = m_map.get(serviceType);

		if (map == null) {
			synchronized (this) {
				map = m_map.get(serviceType);

				if (map == null) {
					map = new HashMap<String, Object>();
					m_map.put(serviceType, map);
				}
			}
		}

		T service = (T) map.get(alias);

		if (service == null) {
			synchronized (map) {
				service = (T) map.get(alias);

				if (service == null) {
					ServiceProvider<T> provider = m_registry.getServiceProvider(serviceType);
					ServiceBinding binding = m_registry.getServiceBinding(serviceType, alias);
					ServiceContext<T> ctx = lookup(ServiceContext.class);

					ctx.setServiceProvider(provider);
					ctx.setServiceBinding(binding);

					m_lifecycle.configure(ctx);

					service = provider.makeService(binding);

					m_lifecycle.start(ctx);
					map.put(alias, service);
				}
			}
		}

		return service;
	}
}

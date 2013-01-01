package com.dianping.service.spi.lifecycle;

import org.unidal.lookup.annotation.Inject;

import com.dianping.service.spi.ServiceConfigurator;

public class DefaultServiceLifecycle implements ServiceLifecycle {
	@Inject
	private ServiceConfigurator m_injector;

	@Override
	public void configure(ServiceContext<?> ctx) throws Exception {
		m_injector.configure(ctx);
	}

	@Override
	public void destroy(ServiceContext<?> ctx) {

	}

	@Override
	public void resume(ServiceContext<?> ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void start(ServiceContext<?> ctx) {

	}

	@Override
	public void stop(ServiceContext<?> ctx) {
		// TODO Auto-generated method stub

	}

	@Override
	public void suspend(ServiceContext<?> ctx) {
		// TODO Auto-generated method stub

	}
}

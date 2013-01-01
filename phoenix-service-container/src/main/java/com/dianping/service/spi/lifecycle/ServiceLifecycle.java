package com.dianping.service.spi.lifecycle;

public interface ServiceLifecycle {
	public void destroy(ServiceContext<?> ctx) throws Exception;

	public void configure(ServiceContext<?> ctx) throws Exception;

	public void resume(ServiceContext<?> ctx) throws Exception;

	public void start(ServiceContext<?> ctx) throws Exception;

	public void stop(ServiceContext<?> ctx) throws Exception;

	public void suspend(ServiceContext<?> ctx) throws Exception;
}

package com.dianping.service.spi;

public interface ServiceProvider<T> {
	public Class<T> getServiceType();

	public T makeService(ServiceBinding binding) throws Exception;

	public void destroyService(T service) throws Exception;
}

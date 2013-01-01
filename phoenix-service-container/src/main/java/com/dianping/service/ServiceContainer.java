package com.dianping.service;

public interface ServiceContainer {
	public <T> T lookup(Class<T> serviceType) throws ServiceNotAvailableException;

	public <T> T lookup(Class<T> serviceType, String alias) throws ServiceNotAvailableException;
}

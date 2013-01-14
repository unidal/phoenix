package com.dianping.service.spi;

public interface ServiceManager {
	public <T> T getService(Class<T> serviceType, String alias) throws Exception;
}

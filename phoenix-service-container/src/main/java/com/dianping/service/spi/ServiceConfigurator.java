package com.dianping.service.spi;

import com.dianping.service.spi.lifecycle.ServiceContext;

public interface ServiceConfigurator {
	public void configure(ServiceContext<?> ctx) throws Exception;
}

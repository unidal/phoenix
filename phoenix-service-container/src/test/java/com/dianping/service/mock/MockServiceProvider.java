package com.dianping.service.mock;

import org.codehaus.plexus.logging.Logger;

import com.dianping.service.spi.ServiceBinding;
import com.dianping.service.spi.ServiceProvider;
import com.dianping.service.spi.annotation.Component;

public class MockServiceProvider implements ServiceProvider<MockService> {
	@Component
	private Logger m_logger;
	
	@Override
	public Class<MockService> getServiceType() {
		return MockService.class;
	}

	@Override
	public MockService makeService(ServiceBinding binding) throws Exception {
		return new MockService(m_logger);
	}

	@Override
	public void destroyService(MockService service) throws Exception {
		// do nothing here
	}
}

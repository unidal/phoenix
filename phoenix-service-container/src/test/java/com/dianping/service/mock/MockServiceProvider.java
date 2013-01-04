package com.dianping.service.mock;

import org.codehaus.plexus.logging.Logger;

import com.dianping.service.spi.ServiceBinding;
import com.dianping.service.spi.ServiceProvider;
import com.dianping.service.spi.annotation.Component;
import com.dianping.service.spi.annotation.Configuration;
import com.dianping.service.spi.annotation.Property;

public class MockServiceProvider implements ServiceProvider<MockService> {
	@Component
	private Logger m_logger;

	@Property(name = "p1")
	private String m_firstProperty;

	@Property(name = "p2")
	private int m_secondProperty;

	@Configuration
	private String m_configuration;

	@Override
	public Class<MockService> getServiceType() {
		return MockService.class;
	}

	@Override
	public MockService makeService(ServiceBinding binding) throws Exception {
		MockService service = new MockService(m_logger);

		service.setFirstProperty(m_firstProperty);
		service.setSecondProperty(m_secondProperty);
		service.setConfiguration(m_configuration);
		return service;
	}

	@Override
	public void destroyService(MockService service) throws Exception {
		// do nothing here
	}
}

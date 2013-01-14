package com.dianping.service.logging;

import static org.codehaus.plexus.logging.Logger.LEVEL_DEBUG;
import static org.codehaus.plexus.logging.Logger.LEVEL_ERROR;
import static org.codehaus.plexus.logging.Logger.LEVEL_INFO;
import static org.codehaus.plexus.logging.Logger.LEVEL_WARN;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;

import com.dianping.service.spi.ServiceBinding;
import com.dianping.service.spi.ServiceProvider;
import com.dianping.service.spi.annotation.Property;

public class PlexusLoggerProvider implements ServiceProvider<Logger>, LogEnabled {
	@Property(defaultValue = "info")
	private String m_threshold;

	private Logger m_logger;

	@Override
	public void destroyService(Logger logger) {
		// do nothing
	}

	@Override
	public void enableLogging(Logger logger) {
		m_logger = logger;
	}

	@Override
	public Class<Logger> getServiceType() {
		return Logger.class;
	}

	@Override
	public Logger makeService(ServiceBinding binding) {
		if ("info".equalsIgnoreCase(m_threshold)) {
			m_logger.setThreshold(LEVEL_INFO);
		} else if ("debug".equalsIgnoreCase(m_threshold)) {
			m_logger.setThreshold(LEVEL_DEBUG);
		} else if ("warn".equalsIgnoreCase(m_threshold)) {
			m_logger.setThreshold(LEVEL_WARN);
		} else if ("error".equalsIgnoreCase(m_threshold)) {
			m_logger.setThreshold(LEVEL_ERROR);
		} else {
			m_logger.setThreshold(LEVEL_INFO);
		}

		return m_logger;
	}
}

package com.dianping.service.logging;

import java.io.File;
import java.net.URL;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.dianping.service.spi.ServiceBinding;
import com.dianping.service.spi.ServiceProvider;
import com.dianping.service.spi.annotation.Property;

public class Log4jLoggerProvider implements ServiceProvider<Logger> {
	@Property
	private String m_config;

	@Property(required = false)
	private String m_threshold;

	@Override
	public void destroyService(Logger logger) {
		// do nothing
	}

	@Override
	public Class<Logger> getServiceType() {
		return Logger.class;
	}

	@Override
	public Logger makeService(ServiceBinding binding) {
		String configFile = getConfigFile();

		DOMConfigurator.configure(configFile);
		Logger logger = LogManager.getRootLogger();

		if (m_threshold != null) {
			if ("info".equalsIgnoreCase(m_threshold)) {
				logger.setLevel(Level.INFO);
			} else if ("debug".equalsIgnoreCase(m_threshold)) {
				logger.setLevel(Level.DEBUG);
			} else if ("warn".equalsIgnoreCase(m_threshold)) {
				logger.setLevel(Level.WARN);
			} else if ("error".equalsIgnoreCase(m_threshold)) {
				logger.setLevel(Level.ERROR);
			} else {
				logger.setLevel(Level.INFO);
			}
		}

		return logger;
	}

	private String getConfigFile() {
		URL url = Thread.currentThread().getContextClassLoader().getResource(m_config);

		if (url == null) {
			if (m_config.startsWith("/")) {
				url = getClass().getResource(m_config);
			} else {
				url = getClass().getResource("/" + m_config);
			}
		}

		String configFile = null;

		if (url == null) {
			if (new File(m_config).canRead()) {
				configFile = m_config;
			} else {
				throw new RuntimeException(String.format("Config(%s) can't be found as java resource or local file!",
				      m_config));
			}
		} else {
			configFile = url.getPath();
		}

		return configFile;
	}
}

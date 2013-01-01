package com.dianping.service.logging;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.service.ServiceContainer;

public class Log4jLoggerTest extends ComponentTestCase {
	@Test
	public void testLogger() throws Exception {
		ServiceContainer container = lookup(ServiceContainer.class);
		Logger logger = container.lookup(Logger.class);

		logger.info("info message here");
		logger.warn("warning message here");
	}
}

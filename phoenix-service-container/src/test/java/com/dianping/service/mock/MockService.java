package com.dianping.service.mock;

import org.codehaus.plexus.logging.Logger;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;

public class MockService {
	private Logger m_logger;

	public MockService(Logger logger) {
		m_logger = logger;
	}

	public String hello(String name) {
		m_logger.info("name=" + name);

		Cat.getProducer().logEvent("mock", "hello", Message.SUCCESS, "name=" + name);

		return String.format("Hello, %s!", name);
	}
}

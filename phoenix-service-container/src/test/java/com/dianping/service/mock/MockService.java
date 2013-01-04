package com.dianping.service.mock;

import org.codehaus.plexus.logging.Logger;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;

public class MockService {
	private Logger m_logger;

	private String m_firstProperty;

	private int m_secondProperty;

	private String m_configuration;

	public MockService(Logger logger) {
		m_logger = logger;
	}

	public String hello(String name) {
		m_logger.info("name is " + name);

		Cat.getProducer().logEvent("mock", "hello", Message.SUCCESS, "name=" + name);

		return String.format("Hello, %s!", name);
	}

	public void setConfiguration(String configuration) {
		m_configuration = configuration;
	}

	public void setFirstProperty(String firstProperty) {
		m_firstProperty = firstProperty;
	}

	public void setSecondProperty(int secondProperty) {
		m_secondProperty = secondProperty;
	}

	public String verbose() {
		return m_firstProperty + ":" + m_secondProperty + ":" + m_configuration;
	}
}

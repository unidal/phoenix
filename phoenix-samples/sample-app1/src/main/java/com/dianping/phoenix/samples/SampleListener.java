package com.dianping.phoenix.samples;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SampleListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("SampleListener Init");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("SampleListener destory");
	}
}

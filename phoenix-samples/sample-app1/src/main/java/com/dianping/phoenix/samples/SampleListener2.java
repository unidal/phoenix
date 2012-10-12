package com.dianping.phoenix.samples;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class SampleListener2 implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("DemoListener"+this.getClass().getName()+" Init");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("DemoListener"+this.getClass().getName()+" destory");
	}
}

package com.dianping.kernel.demo;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DemoListener2 implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("DemoListener"+this.getClass().getName()+" Init");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("DemoListener"+this.getClass().getName()+" destory");
	}
}

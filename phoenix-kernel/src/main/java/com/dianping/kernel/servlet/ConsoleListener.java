package com.dianping.kernel.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.dianping.kernel.console.ApplicationModel;

public class ConsoleListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();

		ctx.setAttribute("phoenix.applicationModel", new ApplicationModel());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();

		ctx.removeAttribute("phoenix.applicationModel");
	}
}

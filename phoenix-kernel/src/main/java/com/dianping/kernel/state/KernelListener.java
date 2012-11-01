package com.dianping.kernel.state;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class KernelListener implements ServletContextListener {
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

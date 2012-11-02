package com.dianping.kernel.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.dianping.kernel.Constants;
import com.dianping.kernel.GlobalModel;

public class InspectListener implements ServletContextListener {
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();

		ctx.setAttribute(Constants.PHOENIX_MODEL_GLOBAL, new GlobalModel());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ServletContext ctx = sce.getServletContext();

		ctx.removeAttribute(Constants.PHOENIX_MODEL_GLOBAL);
	}
}

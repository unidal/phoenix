package com.dianping.phoenix.agent;

import java.io.File;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class StandaloneServer {

	private final static Logger logger = Logger.getLogger(StandaloneServer.class);

	public static void main(String[] args) throws Exception {

		if (args.length != 3) {
			logger.error("usage: port contextPath warRoot");
			return;
		}

		int port = Integer.parseInt(args[0]);
		String contextPath = args[1];
		File warRoot = new File(args[2]);

		logger.info(String.format("starting jetty@%d, contextPath %s, warRoot %s", port, contextPath,
				warRoot.getAbsoluteFile()));

		Server server = new Server(port);
		WebAppContext context = new WebAppContext();

		System.out.println(warRoot.getAbsolutePath());
		context.setContextPath(contextPath);
		context.setDescriptor(new File(warRoot, "WEB-INF/web.xml").getPath());
		context.setResourceBase(warRoot.getPath());

		server.setHandler(context);
		server.start();

	}

}

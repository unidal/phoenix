package com.dianping.phoenix.agent;

import java.io.File;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class PhoenixAgent {

	private final static Logger logger = Logger.getLogger(PhoenixAgent.class);

	public static void main(String[] args) throws Exception {

		if (args.length != 3) {
			logger.error("usage: port contextPath warRoot");
			return;
		}

		int port = Integer.parseInt(args[0]);
		String contextPath = args[1];
		File warRoot = new File(args[2]);

		logger.info(String.format("starting jetty@%d, contextPath %s, warRoot %s", port, contextPath,
				warRoot.getAbsoluteFile().getAbsolutePath()));

		Server server = new Server(port);
		addTerminateSingalHandler(server);
		WebAppContext context = new WebAppContext();

		context.setContextPath(contextPath);
		context.setDescriptor(new File(warRoot, "WEB-INF/web.xml").getPath());
		context.setResourceBase(warRoot.getPath());

		server.setHandler(context);
		server.start();

	}

	public static void addTerminateSingalHandler(final Server server) {
		// not officially supported API 
		Signal.handle(new Signal("TERM"), new SignalHandler() {
			
			@Override
			public void handle(Signal signal) {
				logger.info(String.format("%s signal received, try to stop jetty server", signal));
				try {
					server.stop();
				} catch (Exception e) {
					logger.error("error stop jetty server", e);
				}
			}
		});
	}

}

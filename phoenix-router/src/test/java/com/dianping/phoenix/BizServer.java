package com.dianping.phoenix;

import java.io.File;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class BizServer {

	public static void main(String[] args) throws Exception {
		Server server = new Server(9090);

		// addContext(server, new
		// File("/Users/marsqing/Projects/tmp/webapp/app1"), "/_app1");
		// addContext(server, new
		// File("/Users/marsqing/Projects/tmp/webapp/app2"), "/_shop-web");

		addContext(server, new File("/Users/marsqing/Projects/tmp/webapp/user-service.war"), "/_user-service");
		addContext(server, new File("/Users/marsqing/Projects/tmp/webapp/user-base-service.war"), "/_user-base-service");
		addContext(server, new File("/Users/marsqing/Projects/tmp/webapp/user-web.war"), "/_user-web");
		addContext(server, new File("/Users/marsqing/Projects/tmp/webapp/shop-web.war"), "/_shop-web");
		addContext(server, new File("/Users/marsqing/Projects/tmp/webapp/shoplist-web.war"), "/_shoplist-web");
		addContext(server, new File("/Users/marsqing/Projects/tmp/webapp/dpindex-web.war"), "/_dpindex-web");

		server.start();
		System.in.read();
		server.stop();
	}

	public static void addContext(Server server, File warRoot, String path) {
		WebAppContext context = new WebAppContext();
		context.setContextPath(path);
		context.setDescriptor(new File(warRoot, "WEB-INF/web.xml").getPath());
		context.setResourceBase(warRoot.getPath());
		server.addHandler(context);
	}

}

package com.dianping.phoenix;

import java.io.File;

import org.mortbay.jetty.Server;

public class RouteServer {

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);

		BizServer.addContext(server, new File(new File("."), "src/main/webapp/"), "/");

		server.start();
		System.in.read();
		server.stop();
	}

}

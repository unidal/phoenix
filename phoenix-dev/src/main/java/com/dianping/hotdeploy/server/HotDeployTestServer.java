package com.dianping.hotdeploy.server;

import java.io.File;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppClassLoader;
import org.mortbay.jetty.webapp.WebAppContext;

import com.dianping.hotdeploy.thread.ClassFileWatchThread;

public class HotDeployTestServer {
	public static void main(String[] args) throws Exception {
		Server server = new Server(9090);

		String cPath = "/Users/icloud/workspace/sample-webapp1/";

		addContext(server, cPath, "/_app1");

		server.start();

		System.in.read();
		server.stop();
		System.exit(0);
	}

	public static void addContext(Server server, String srcRoot, String path) {
		try {
			ClassFileWatchThread t = new ClassFileWatchThread(srcRoot);
			t.start();

			WebAppContext context = new WebAppContext();
			context.setContextPath(path);
			context.setDescriptor(new File(srcRoot, "/src/main/webapp/WEB-INF/web.xml").getPath());
			context.setResourceBase(new File(srcRoot, "/src/main/webapp/").getPath());

			WebAppClassLoader wcl = new WebAppClassLoader(t.getClassloader(), context);
			context.setClassLoader(wcl);
			server.addHandler(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

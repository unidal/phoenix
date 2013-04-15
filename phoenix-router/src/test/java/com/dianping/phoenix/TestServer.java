package com.dianping.phoenix;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.util.MultiException;

public class TestServer {

	public static void main(String[] args) throws Exception {
		Server server = new Server(9090);

		// addContext(server, new
		// File("/Users/marsqing/Projects/tmp/webapp/app1"), "/_user-web");
		// addContext(server, new
		// File("/Users/marsqing/Projects/tmp/webapp/app2"), "/_shop-web");

		ContextHandlerCollection ctxs = new ContextHandlerCollection() {
			protected void doStart() throws Exception {
				final MultiException mex = new MultiException();
				final CountDownLatch latch = new CountDownLatch(getHandlers().length);
				if (getHandlers() != null) {
					for (int i = 0; i < getHandlers().length; i++) {
						final Handler h = getHandlers()[i];
						new Thread() {
							public void run() {
								try {
									System.out.println("============" + ((WebAppContext) h).getContextPath());
									h.start();
								} catch (Throwable e) {
									mex.add(e);
								} finally {
									latch.countDown();
								}
							}
						}.start();
					}
				}
				latch.await();
				super.doStart();
				mex.ifExceptionThrow();
			}
		};

//		addContext(ctxs, server, new File("/Users/marsqing/Projects/tmp/webapp/user-service.war"), "/_user-service");
//		addContext(ctxs, server, new File("/Users/marsqing/Projects/tmp/webapp/user-base-service.war"),
//				"/_user-base-service");
//		addContext(ctxs, server, new File("/Users/marsqing/Projects/tmp/webapp/user-web.war"), "/_user-web");
//		addContext(ctxs, server, new File("/Users/marsqing/Projects/tmp/webapp/shop-web.war"), "/_shop-web");
//		addContext(ctxs, server, new File("/Users/marsqing/Projects/tmp/webapp/shoplist-web.war"), "/_shoplist-web");
//		addContext(ctxs, server, new File("/Users/marsqing/Projects/tmp/webapp/dpindex-web.war"), "/_dpindex-web");
		addContext(ctxs, server, new File("/Volumes/HDD/dev_env_work/war/alpaca.war"), "_alpaca");

		addContext(ctxs, server, new File(new File("."), "src/main/webapp/"), "/");

//		server.setHandler(ctxs);
		server.start();
		server.join();
	}

	private static void addContext(ContextHandlerCollection ctxs, Server server, File warRoot, String path) {
		WebAppContext context = new WebAppContext();
		context.setContextPath(path);
		context.setDescriptor(new File(warRoot, "WEB-INF/web.xml").getPath());
		context.setResourceBase(warRoot.getPath());
		server.addHandler(context);
//		ctxs.addHandler(context);
	}

}

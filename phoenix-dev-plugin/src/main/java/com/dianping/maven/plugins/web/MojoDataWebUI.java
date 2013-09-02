/**
 * Project: phoenix-maven-plugin
 * 
 * File Created at 2013-6-20
 * $Id$
 * 
 * Copyright 2010 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.maven.plugins.web;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.unidal.test.browser.Browser;
import org.unidal.test.browser.DefaultBrowser;

/**
 * @author Leo Liang
 * 
 */
public class MojoDataWebUI<T, R> {
	private static final int DEFUALT_PORT = 7463;
	private int port = DEFUALT_PORT;
	private Map<String, BaseMojoDataServlet<T, R>> servletMapping;

	public MojoDataWebUI(int port, Map<String, BaseMojoDataServlet<T, R>> servletMapping) {
		this.port = port;
		this.servletMapping = servletMapping;
	}

	public MojoDataWebUI(Map<String, BaseMojoDataServlet<T, R>> servletMapping) {
		this(DEFUALT_PORT, servletMapping);
	}

	public void start() throws Exception {
		Server server = new Server(port);
		addContext(server, servletMapping);
		server.start();
	}

	private void addContext(Server server, Map<String, BaseMojoDataServlet<T, R>> servletMapping) {
		String warRoot = this.getClass().getResource("/webapp").toExternalForm();
		System.out.println(warRoot);
		WebAppContext context = new WebAppContext();
		context.setContextPath("/");
		context.setResourceBase(warRoot);

		for (Map.Entry<String, BaseMojoDataServlet<T, R>> entry : servletMapping.entrySet()) {
			context.addServlet(new ServletHolder(entry.getValue()), entry.getKey());
		}

		server.addHandler(context);
	}

	public void display(String displayPageUri) throws MalformedURLException {
		StringBuilder sb = new StringBuilder(256);
		Browser browser = new DefaultBrowser();

		sb.append("http://localhost:").append(port).append(displayPageUri);
		browser.display(new URL(sb.toString()));
	}

	public static class DataTransmitter<T, R> {
		private T initData;
		private AtomicReference<R> resultRef = new AtomicReference<R>();
		private CountDownLatch latch = new CountDownLatch(1);

		public DataTransmitter(T initData) {
			this.initData = initData;
		}

		public T getInitData() {
			return initData;
		}

		public void returnResult(R result) {
			resultRef.set(result);
			latch.countDown();
		}

		public R awaitResult() throws InterruptedException {
			latch.await();
			return resultRef.get();
		}
	}

}

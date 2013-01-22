package com.dianping.phoenix.agent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.unidal.helper.Urls;
import org.unidal.lookup.util.StringUtils;

import com.dianping.phoenix.agent.response.entity.Response;
import com.dianping.phoenix.agent.response.transform.DefaultJsonParser;
import com.site.helper.Files.IO;

public class PhoenixAgentDryRun {

	private final static Logger logger = Logger.getLogger(PhoenixAgentDryRun.class);
	private final static int ERROR_EXIT_VALUE = 1;

	public static void main(String[] args) {
		try {
			innerMain(args);
		} catch (Exception e) {
			logger.error("error start agent try run", e);
			System.exit(ERROR_EXIT_VALUE);
		}
	}

	private static void innerMain(String[] args) throws Exception {

		if (args.length < 3) {
			logger.error("usage: port contextPath warRoot");
			System.exit(ERROR_EXIT_VALUE);
		}

		int port = Integer.parseInt(args[0]);
		String contextPath = args[1];
		File warRoot = new File(args[2]);

		int sleepBeforeCheck = 2000;
		if (args.length > 3) {
			sleepBeforeCheck = Integer.parseInt(args[3]);
		}

		int connectTimeout = 2000;
		if (args.length > 4) {
			connectTimeout = Integer.parseInt(args[4]);
		}

		int readTimeout = 5000;
		if (args.length > 5) {
			readTimeout = Integer.parseInt(args[5]);
		}

		logger.info(String.format("starting jetty@%d, contextPath %s, warRoot %s", port, contextPath, warRoot
				.getAbsoluteFile().getAbsolutePath()));

		Server server = new Server(port);
		WebAppContext context = new WebAppContext();

		context.setContextPath(contextPath);
		context.setDescriptor(new File(warRoot, "WEB-INF/web.xml").getPath());
		context.setResourceBase(warRoot.getPath());

		server.setHandler(context);
		server.start();

		Thread.sleep(sleepBeforeCheck);
		System.exit(serverStatusOk(port, contextPath, connectTimeout, readTimeout) ? 0 : ERROR_EXIT_VALUE);

	}

	private static boolean serverStatusOk(int port, String contextPath, int connectTimeout, int readTimeout)
			throws IOException {
		String statusUrl = "http://"
				+ String.format("127.0.0.1:%d%s/agent/deploy", port, contextPath).replaceAll("//", "/");
		logger.info(String.format("check server status via %s", statusUrl));
		InputStream statusJsonIn = Urls.forIO().connectTimeout(connectTimeout).readTimeout(readTimeout)
				.openStream(statusUrl);

		String statusJson = IO.INSTANCE.readFrom(statusJsonIn, "utf-8");
		logger.info(String.format("got status json %s", statusJson));
		return responseJsonOk(statusJson);
	}

	static boolean responseJsonOk(String statusJson) {
		Response res = null;
		try {
			res = DefaultJsonParser.parse(statusJson);
		} catch (Exception e) {
			logger.error("error parse status json", e);
		}
		logger.info(String.format("parse to Response %s and ip %s", res, res == null ? null : res.getIp()));
		return res != null && StringUtils.isNotEmpty(res.getIp());
	}

}

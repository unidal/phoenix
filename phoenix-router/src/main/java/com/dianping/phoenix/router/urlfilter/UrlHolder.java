package com.dianping.phoenix.router.urlfilter;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class UrlHolder {

	private String protocol;
	private String host;
	private int port;
	private String path;
	private String query;

	public UrlHolder(URL url) {
		protocol = url.getProtocol();
		host = url.getHost();
		port = url.getPort();
		path = url.getPath();
		query = url.getQuery();
	}

	public UrlHolder(HttpServletRequest req) {
		// TODO may be change parameter type
		protocol = req.getScheme();
		host = "127.0.0.1";
		port = req.getLocalPort();
		path = req.getRequestURI();
		query = req.getQueryString();
	}

	public UrlHolder() {
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String toUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(protocol);
		sb.append("://");
		sb.append(host);
		if (port > 0) {
			sb.append(":");
			sb.append(port);
		}
		sb.append(path);
		if (!StringUtils.isBlank(query)) {
			sb.append(query);
		}
		return sb.toString();
	}

}

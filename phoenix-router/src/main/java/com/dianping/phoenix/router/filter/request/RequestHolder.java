package com.dianping.phoenix.router.filter.request;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class RequestHolder {

	private String protocol;
	private String host;
	private int port;
	private String path;
	private String query;
	private Map<String, List<String>> headers = new HashMap<String, List<String>>();

	public RequestHolder(URL url, Map<String, List<String>> headers) {
		protocol = url.getProtocol();
		host = url.getHost();
		port = url.getPort();
		path = url.getPath();
		query = url.getQuery();
		this.headers = headers;
	}

	@SuppressWarnings("unchecked")
	public RequestHolder(HttpServletRequest req) {
		protocol = req.getScheme();
		// the first filter F5 filter will replace host with target pool
		host = "127.0.0.1";
		port = req.getLocalPort();
		path = req.getRequestURI();
		query = req.getQueryString();
		Enumeration<String> names = req.getHeaderNames();
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			Enumeration<String> values = req.getHeaders(name);
			List<String> valueList = new ArrayList<String>();
			while (values.hasMoreElements()) {
				valueList.add(values.nextElement());
			}
			headers.put(name, valueList);
		}
	}

	public RequestHolder() {
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

	public Map<String, List<String>> getHeaders() {
		return headers;
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

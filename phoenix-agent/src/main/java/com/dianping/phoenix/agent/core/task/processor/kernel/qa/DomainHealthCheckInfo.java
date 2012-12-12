package com.dianping.phoenix.agent.core.task.processor.kernel.qa;

public class DomainHealthCheckInfo {

	private String domain;
	private String env;
	private String host;
	private int port;
	private String qaServiceUrlPrefix;

	public DomainHealthCheckInfo(String domain, String env, String host, int port, String qaServiceUrlPrefix) {
		this.domain = domain;
		this.env = env;
		this.host = host;
		this.port = port;
		this.qaServiceUrlPrefix = qaServiceUrlPrefix;
	}

	public String getDomain() {
		return domain;
	}

	public String getEnv() {
		return env;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getQaServiceUrlPrefix() {
		return qaServiceUrlPrefix;
	}

}

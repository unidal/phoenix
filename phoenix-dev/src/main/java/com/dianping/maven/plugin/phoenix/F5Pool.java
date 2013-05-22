package com.dianping.maven.plugin.phoenix;

public class F5Pool {

	private String projectName;
	private String poolName;
	private String url;

	public F5Pool() {
	}

	public F5Pool(String projectName, String poolName, String url) {
		this.projectName = projectName;
		this.poolName = poolName;
		this.url = url;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}

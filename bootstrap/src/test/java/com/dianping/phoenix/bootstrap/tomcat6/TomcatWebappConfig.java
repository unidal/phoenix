package com.dianping.phoenix.bootstrap.tomcat6;

import java.util.List;

/**
 * Configurations of a tomcat webapp
 * @author marsqing
 *
 */
public class TomcatWebappConfig {

	private String webappDir;
	private List<String> classesDir;
	private String contextPath;
	
	public TomcatWebappConfig() {
	}

	/**
	 * 
	 * @param webappDir directory which contains standard WEB-INF/web.xml
	 * @param classesDir extra directory or jar to add to classpath of webapp
	 * @param contextPath context path of webapp, "" for root, do not add leading "/"
	 */
	public TomcatWebappConfig(String webappDir, List<String> classesDir, String contextPath) {
		this.webappDir = webappDir;
		this.classesDir = classesDir;
		this.contextPath = contextPath;
	}
	public String getWebappDir() {
		return webappDir;
	}
	public void setWebappDir(String webappDir) {
		this.webappDir = webappDir;
	}
	public List<String> getClassesDir() {
		return classesDir;
	}
	public void setClassesDir(List<String> classesDir) {
		this.classesDir = classesDir;
	}
	public String getContextPath() {
		return contextPath;
	}
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	
	
	
}

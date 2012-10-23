package com.dianping.kernel.console.page.classpath;

public enum JspFile {
	VIEW("/jsp/console/classpath.jsp"),

	;

	private String m_path;

	private JspFile(String path) {
		m_path = path;
	}

	public String getPath() {
		return m_path;
	}
}

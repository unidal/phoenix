package com.dianping.kernel.inspect.page.classpath;

public enum JspFile {
	VIEW("/jsp/inspect/classpath.jsp"),

	;

	private String m_path;

	private JspFile(String path) {
		m_path = path;
	}

	public String getPath() {
		return m_path;
	}
}

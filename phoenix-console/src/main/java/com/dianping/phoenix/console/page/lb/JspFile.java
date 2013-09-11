package com.dianping.phoenix.console.page.lb;

public enum JspFile {
	VIEW("/jsp/console/lb.jsp"),

	;

	private String m_path;

	private JspFile(String path) {
		m_path = path;
	}

	public String getPath() {
		return m_path;
	}
}

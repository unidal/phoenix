package com.dianping.phoenix.console.page.deploy2;

public enum JspFile {
	VIEW("/jsp/console/deploy2.jsp"),

	;

	private String m_path;

	private JspFile(String path) {
		m_path = path;
	}

	public String getPath() {
		return m_path;
	}
}

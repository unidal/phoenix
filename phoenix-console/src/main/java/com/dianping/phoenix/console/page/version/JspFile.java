package com.dianping.phoenix.console.page.version;

public enum JspFile {
	VIEW("/jsp/console/version.jsp"),
	STATUS("/jsp/console/version_status.jsp")
	;

	private String m_path;

	private JspFile(String path) {
		m_path = path;
	}

	public String getPath() {
		return m_path;
	}
}

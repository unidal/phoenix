package com.dianping.phoenix.agent.page.deploy;

public enum JspFile {
	VIEW("/jsp/agent/deploy.jsp"),

	;

	private String m_path;

	private JspFile(String path) {
		m_path = path;
	}

	public String getPath() {
		return m_path;
	}
}

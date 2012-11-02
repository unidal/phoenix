package com.dianping.kernel.inspect.page.descriptor;

public enum JspFile {
	VIEW("/jsp/inspect/descriptor.jsp"),

	;

	private String m_path;

	private JspFile(String path) {
		m_path = path;
	}

	public String getPath() {
		return m_path;
	}
}

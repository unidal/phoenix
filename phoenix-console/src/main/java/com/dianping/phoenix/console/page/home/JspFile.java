package com.dianping.phoenix.console.page.home;

public enum JspFile {
	HOME("/jsp/console/home.jsp"),

	PROJECT("/jsp/console/project.jsp"),

	ABOUT("/jsp/console/about.jsp"),

	SEARCHJAR("/jsp/console/search_jar.jsp"),

	SEARCHAGENT("/jsp/console/search_agent.jsp"),

	OVERVIEW("/jsp/console/overview.jsp"),

	DOMAININFO("/jsp/console/overview_inner.jsp"),

	DEPLOY("/jsp/console/deploy.jsp");

	private String m_path;

	private JspFile(String path) {
		m_path = path;
	}

	public String getPath() {
		return m_path;
	}
}

package com.dianping.phoenix.service;

import com.dianping.phoenix.console.dal.deploy.Deliverable;

public class GitContext {
	private String m_type;

	private String m_version;

	private String m_description;

	public GitContext(Deliverable d) {
		m_type = d.getWarType();
		m_version = d.getWarVersion();
		m_description = d.getDescription();
	}

	public GitContext(String type, String version, String desception) {
		m_type = type;
		m_version = version;
		m_description = desception;
	}

	public String getDescription() {
		return m_description;
	}

	public String getType() {
		return m_type;
	}

	public String getVersion() {
		return m_version;
	}
}

package com.dianping.phoenix.version;

import com.dianping.phoenix.console.dal.deploy.Deliverable;

public class VersionContext {
	private String m_type;

	private int m_versionId;

	private String m_version;

	private String m_description;

	private String m_releaseNotes;

	private String m_createdBy;

	public VersionContext(Deliverable d) {
		m_versionId = d.getId();
		m_type = d.getWarType();
		m_version = d.getWarVersion();
		m_description = d.getDescription();
		m_releaseNotes = d.getReleaseNotes();
		m_createdBy = d.getCreatedBy();
	}

	public VersionContext(String type, int versionId, String version, String desception, String releaseNotes,
	      String createdBy) {
		m_versionId = versionId;
		m_version = version;
		m_description = desception;
		m_releaseNotes = releaseNotes;
		m_createdBy = createdBy;
	}

	public String getCreatedBy() {
		return m_createdBy;
	}

	public String getDescription() {
		return m_description;
	}

	public String getReleaseNotes() {
		return m_releaseNotes;
	}

	public String getType() {
		return m_type;
	}

	public String getVersion() {
		return m_version;
	}

	public int getVersionId() {
		return m_versionId;
	}
}

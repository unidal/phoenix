package com.dianping.phoenix.version;

public class VersionContext {

	private int m_versionId;
	private String m_version;
	private String m_desception;
	private String m_releaseNotes;
	private String m_createdBy;

	public VersionContext(int versionId, String version, String desception,
			String releaseNotes, String createdBy) {
		m_versionId = versionId;
		m_version = version;
		m_desception = desception;
		m_releaseNotes = releaseNotes;
		m_createdBy = createdBy;
	}

	public String getCreatedBy() {
		return m_createdBy;
	}

	public String getDesception() {
		return m_desception;
	}

	public String getReleaseNotes() {
		return m_releaseNotes;
	}

	public String getVersion() {
		return m_version;
	}

	public int getVersionId() {
		return m_versionId;
	}

	public void setCreatedBy(String createdBy) {
		m_createdBy = createdBy;
	}

	public void setDesception(String desception) {
		m_desception = desception;
	}

	public void setReleaseNotes(String releaseNotes) {
		m_releaseNotes = releaseNotes;
	}

	public void setVersion(String version) {
		m_version = version;
	}

	public void setVersionId(int versionId) {
		m_versionId = versionId;
	}

}

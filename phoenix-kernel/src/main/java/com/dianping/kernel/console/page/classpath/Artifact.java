package com.dianping.kernel.console.page.classpath;

public class Artifact {
	private String m_groupId;

	private String m_artifactId;

	private String m_version;

	private String m_path;

	public Artifact(String path, String groupId, String artifactId, String version) {
		m_path = path;
		m_groupId = groupId;
		m_artifactId = artifactId;
		m_version = version;
	}

	public String getArtifactId() {
		return m_artifactId;
	}

	public String getGroupId() {
		return m_groupId;
	}

	public String getPath() {
		return m_path;
	}

	public String getVersion() {
		return m_version;
	}

	@Override
	public String toString() {
		return String.format("%s:%s:%s", m_groupId, m_artifactId, m_version);
	}
}
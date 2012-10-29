package com.dianping.kernel.console.page.classpath;

import java.io.File;

public class Artifact implements Comparable<Artifact> {
	private String m_groupId;

	private String m_artifactId;

	private String m_version;

	private String m_path;

	public Artifact(String path) {
		m_path = path;
		m_artifactId = isFromKernel() ? new File(path).getName() + "(phoenix-kernel)" : new File(path).getName();
		m_version = "&lt;none&gt;";
	}

	public Artifact(String path, String groupId, String artifactId, String version) {
		m_path = path;
		m_groupId = groupId;
		m_artifactId = artifactId;
		m_version = version;
	}

	@Override
	public int compareTo(Artifact o) {
		if (m_groupId != null && o.m_groupId != null) {
			int val = m_groupId.compareTo(o.m_groupId);

			if (val != 0) {
				return val;
			}
		} else if (m_groupId != null) {
			return -1; // null first
		} else if (o.m_groupId != null) {
			return 1; // null first
		}

		if (m_artifactId != null && o.m_artifactId != null) {
			int val = m_artifactId.compareTo(o.m_artifactId);

			if (val != 0) {
				return val;
			}
		} else if (m_artifactId != null) {
			return -1; // null first
		} else if (o.m_artifactId != null) {
			return 1; // null first
		}

		if (m_version != null && o.m_version != null) {
			int val = m_version.compareTo(o.m_version);

			if (val != 0) {
				return val;
			}
		} else if (m_version != null) {
			return -1; // null first
		} else if (o.m_version != null) {
			return 1; // null first
		}

		return 0;
	}

	public String getArtifactId() {
		return m_artifactId;
	}

	public String getGroupId() {
		return m_groupId;
	}

	public String getKey() {
		return m_artifactId;
	}

	public String getPath() {
		return m_path;
	}

	public String getVersion() {
		return m_version;
	}

	public boolean isFromKernel() {
		return m_path.contains("/phoenix-kernel/");
	}

	@Override
	public String toString() {
		return String.format("%s:%s:%s", m_groupId, m_artifactId, m_version);
	}
}
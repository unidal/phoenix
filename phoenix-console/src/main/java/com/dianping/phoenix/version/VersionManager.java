package com.dianping.phoenix.version;

import java.util.List;

import org.unidal.dal.jdbc.DalException;

import com.dianping.phoenix.console.dal.deploy.Version;
import com.dianping.phoenix.service.StatusReporter;

public interface VersionManager {
	void clearVersion(String version);

	Version createVersion(String version, String description, String releaseNotes, String createdBy) throws Exception;

	Version getActiveVersion() throws Exception;

	List<Version> getFinishedVersions() throws Exception;

	StatusReporter getReporter();

	VersionLog getStatus(String version, int index);

	void removeVersion(int id) throws Exception;

	Version store(String version, String description, String releaseNotes, String createdBy) throws DalException;

	void submitVersion(VersionContext context) throws VersionException;

	void updateVersionSuccessed(int versionId) throws DalException;

	public static class VersionLog {
		private int m_index;

		private List<String> m_messages;

		public VersionLog(int index, List<String> messages) {
			m_index = index;
			m_messages = messages;
		}

		public int getIndex() {
			return m_index;
		}

		public List<String> getMessages() {
			return m_messages;
		}

		public void setIndex(int m_index) {
			this.m_index = m_index;
		}

		public void setMessages(List<String> messages) {
			this.m_messages = messages;
		}
	}
}

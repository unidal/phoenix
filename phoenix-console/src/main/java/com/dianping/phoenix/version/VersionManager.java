package com.dianping.phoenix.version;

import java.util.List;

import org.unidal.dal.jdbc.DalException;

import com.dianping.phoenix.console.dal.deploy.Deliverable;
import com.dianping.phoenix.service.StatusReporter;

public interface VersionManager {
	public void clearVersion(String version);

	public Deliverable addVersion(String type, String version, String description, String releaseNotes,
	      String createdBy) throws Exception;

	public String getActiveVersion(String warType) throws Exception;

	public List<Deliverable> getFinishedVersions(String warType) throws Exception;

	public StatusReporter getReporter();

	public VersionLog getStatus(String version, int index);

	public void removeVersion(int id) throws Exception;

	public void submitVersion(VersionContext context) throws VersionException;

	public void updateVersionStatus(int id, VersionStatus status) throws DalException;

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
	}
}

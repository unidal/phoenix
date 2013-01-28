package com.dianping.phoenix.version;

public enum VersionStatus {
	CREATIING(1, "creating"),

	ACTIVE(2, "active"),

	REMOVED(3, "removed");

	private int m_id;

	private String m_name;

	private VersionStatus(int id, String name) {
		m_id = id;
		m_name = name;
	}

	public static VersionStatus getById(int id, VersionStatus defaultStatus) {
		for (VersionStatus status : VersionStatus.values()) {
			if (status.getId() == id) {
				return status;
			}
		}

		return defaultStatus;
	}

	public static VersionStatus getByName(String name, VersionStatus defaultStatus) {
		for (VersionStatus status : VersionStatus.values()) {
			if (status.getName().equals(name)) {
				return status;
			}
		}

		return defaultStatus;
	}

	public int getId() {
		return m_id;
	}

	public String getName() {
		return m_name;
	}

	public String getTitle() {
		return m_name.toUpperCase();
	}
}

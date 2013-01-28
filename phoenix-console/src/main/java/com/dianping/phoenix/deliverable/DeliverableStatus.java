package com.dianping.phoenix.deliverable;

public enum DeliverableStatus {
	CREATIING(1, "creating"),

	ACTIVE(2, "active"),

	REMOVED(3, "removed");

	private int m_id;

	private String m_name;

	private DeliverableStatus(int id, String name) {
		m_id = id;
		m_name = name;
	}

	public static DeliverableStatus getById(int id, DeliverableStatus defaultStatus) {
		for (DeliverableStatus status : DeliverableStatus.values()) {
			if (status.getId() == id) {
				return status;
			}
		}

		return defaultStatus;
	}

	public static DeliverableStatus getByName(String name, DeliverableStatus defaultStatus) {
		for (DeliverableStatus status : DeliverableStatus.values()) {
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

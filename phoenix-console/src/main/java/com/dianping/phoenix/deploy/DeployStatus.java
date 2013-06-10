package com.dianping.phoenix.deploy;

public enum DeployStatus {
	SUCCESS(3, "successful"), // completed with all successful

	DEPLOYING(2, "deploying"),

	WARNING(4, "warning"), // completed with partial failures

	CANCELLING(10, "cancelling"),

	PAUSING(11, "pausing"),

	UNKNOWN(999, "unknown");

	private int m_id;

	private String m_name;

	private DeployStatus(int id, String name) {
		m_id = id;
		m_name = name;
	}

	public static DeployStatus getById(int id, DeployStatus defaultStatus) {
		for (DeployStatus status : DeployStatus.values()) {
			if (status.getId() == id) {
				return status;
			}
		}

		return defaultStatus;
	}

	public static DeployStatus getByName(String name, DeployStatus defaultStatus) {
		for (DeployStatus status : DeployStatus.values()) {
			if (status.getName().equals(name)) {
				return status;
			}
		}

		return defaultStatus;
	}

	public static boolean isFinalStatus(DeployStatus status) {
		return status == SUCCESS || status == WARNING;
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

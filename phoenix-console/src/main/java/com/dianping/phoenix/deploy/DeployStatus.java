package com.dianping.phoenix.deploy;

public enum DeployStatus {
	PENDING("pending"),

	DOING("doing"),

	SUCCESS("success"),

	FAILED("failed");

	private String m_name;

	private DeployStatus(String name) {
		m_name = name;
	}

	public static DeployStatus getByName(String name, DeployStatus defaultStatus) {
		for (DeployStatus status : DeployStatus.values()) {
			if (status.getName().equals(name)) {
				return status;
			}
		}

		return defaultStatus;
	}

	public String getName() {
		return m_name;
	}
}

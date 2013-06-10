package com.dianping.phoenix.deploy.agent;

public enum AgentStatus {
	SUCCESS(0, "successful"),

	PENDING(1, "pending"),

	DEPLOYING(2, "deploying"),

	WARNING(3, "warning"),

	FAILED(-1, "failed"),

	CANCELLED(-2, "cancelled"),

	ABORTED(-3, "aborted"),

	;

	private int m_id;

	private String m_name;

	private AgentStatus(int id, String name) {
		m_id = id;
		m_name = name;
	}

	public static AgentStatus getById(int id, AgentStatus defaultStatus) {
		for (AgentStatus status : AgentStatus.values()) {
			if (status.getId() == id) {
				return status;
			}
		}

		return defaultStatus;
	}

	public static AgentStatus getByName(String name, AgentStatus defaultStatus) {
		for (AgentStatus status : AgentStatus.values()) {
			if (status.getName().equals(name)) {
				return status;
			}
		}

		return defaultStatus;
	}

	public static boolean isFinalStatus(AgentStatus status) {
		return status == SUCCESS || status == FAILED;
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

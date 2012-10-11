package com.dianping.phoenix.console.service;

public enum DeployStep {
	PREPARE("prepare", 0, 10),

	ACTIVATE("activate", 1, 50),

	TEST("test", 2, 20),

	COMMIT_ROLLBACK("commit", 3, 20);

	private String m_name;

	private int m_step;

	private int m_weight;

	private DeployStep(String name, int step, int weight) {
		m_name = name;
		m_step = step;
		m_weight = weight;
	}

	public static DeployStep getByName(String name, DeployStep defaultStatus) {
		for (DeployStep action : DeployStep.values()) {
			if (action.getName().equals(name)) {
				return action;
			}
		}

		return defaultStatus;
	}

	public String getName() {
		return m_name;
	}

	public int getStep() {
		return m_step;
	}

	public int getWeight() {
		return m_weight;
	}
}

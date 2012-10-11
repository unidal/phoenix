package com.dianping.phoenix.console.service;


public enum Action {
	PREPARE("prepare", DeployStep.PREPARE),

	ACTIVATE("activate", DeployStep.ACTIVATE),

	TEST("test", DeployStep.TEST),

	COMMIT("commit", DeployStep.COMMIT_ROLLBACK),

	ROLLBACK("rollback", DeployStep.COMMIT_ROLLBACK),

	DEFAULT("default", null);

	private String m_name;

	private DeployStep m_step;

	private Action(String name, DeployStep step) {
		m_name = name;
		m_step = step;
	}

	public DeployStep getDeployStep() {
		return m_step;
	}

}

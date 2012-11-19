package com.dianping.phoenix.agent.page.deploy;

public enum Action implements org.unidal.web.mvc.Action {
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

	public static Action getByName(String name, Action defaultAction) {
		for (Action action : Action.values()) {
			if (action.getName().equals(name)) {
				return action;
			}
		}

		return defaultAction;
	}

	public DeployStep getDeployStep() {
		return m_step;
	}

	@Override
	public String getName() {
		return m_name;
	}
}

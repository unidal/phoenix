package com.dianping.phoenix.console.page.deploy;

public enum Action implements org.unidal.web.mvc.Action {
	PAUSE("pause"),
	
	CONTINUE("continue"),
	
	CANCEL("cancel"),
	
	VIEW("view"),
	
	STATUS("status");

	private String m_name;

	private Action(String name) {
		m_name = name;
	}

	public static Action getByName(String name, Action defaultAction) {
		for (Action action : Action.values()) {
			if (action.getName().equals(name)) {
				return action;
			}
		}

		return defaultAction;
	}

	@Override
	public String getName() {
		return m_name;
	}
}

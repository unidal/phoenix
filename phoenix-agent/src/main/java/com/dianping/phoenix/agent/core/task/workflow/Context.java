package com.dianping.phoenix.agent.core.task.workflow;

import java.io.OutputStream;

public class Context {

	private OutputStream logOut;
	private boolean killed;

	public boolean isKilled() {
		return killed;
	}

	public OutputStream getLogOut() {
		return logOut;
	}

}

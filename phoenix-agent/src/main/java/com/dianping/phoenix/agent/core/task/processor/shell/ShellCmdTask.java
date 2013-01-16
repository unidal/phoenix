package com.dianping.phoenix.agent.core.task.processor.shell;

import com.dianping.phoenix.agent.core.task.AbstractTask;

public class ShellCmdTask extends AbstractTask {

	private String cmd;

	/**
	 * for serialization
	 */
	@SuppressWarnings("unused")
	private ShellCmdTask() {
	}

	public ShellCmdTask(String cmd) {
		this.cmd = cmd;
	}

	public String getCmd() {
		return cmd;
	}

}

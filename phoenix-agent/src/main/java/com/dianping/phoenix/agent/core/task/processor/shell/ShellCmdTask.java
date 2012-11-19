package com.dianping.phoenix.agent.core.task.processor.shell;

import com.dianping.phoenix.agent.core.task.AbstractTask;

public class ShellCmdTask extends AbstractTask {

	private String cmd;

	public ShellCmdTask(String cmd) {
		this.cmd = cmd;
	}

	public String getCmd() {
		return cmd;
	}

}

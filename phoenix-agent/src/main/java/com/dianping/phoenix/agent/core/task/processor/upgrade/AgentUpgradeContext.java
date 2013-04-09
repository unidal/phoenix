package com.dianping.phoenix.agent.core.task.processor.upgrade;

import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.shell.ScriptExecutor;
import com.dianping.phoenix.agent.core.task.workflow.Context;

public class AgentUpgradeContext extends Context {
	@Inject
	private AgentUpgradeStepProvider stepProvider;
	@Inject
	private ScriptExecutor scriptExecutor;

	private String underLyingFile;
	private String tempScriptFile = "phoenix-agent-self-upgrade.sh." + System.currentTimeMillis();

	public String getUnderLyingFile() {
		return underLyingFile;
	}

	public void setUnderLyingFile(String underLyingFile) {
		this.underLyingFile = underLyingFile;
	}

	public AgentUpgradeStepProvider getStepProvider() {
		return stepProvider;
	}

	public ScriptExecutor getScriptExecutor() {
		return scriptExecutor;
	}

	public String getTempScriptFile() {
		return tempScriptFile;
	}
}

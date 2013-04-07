package com.dianping.phoenix.agent.core.task.processor.upgrade;

import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.task.workflow.Context;

public class AgentUpgradeContext extends Context {
	@Inject
	private AgentUpgradeStepProvider stepProvider;

	private AgentUpgradeTask task;
	private String underLyingFile;

	public AgentUpgradeTask getTask() {
		return task;
	}

	public void setTask(AgentUpgradeTask task) {
		this.task = task;
	}

	public String getUnderLyingFile() {
		return underLyingFile;
	}

	public void setUnderLyingFile(String underLyingFile) {
		this.underLyingFile = underLyingFile;
	}

	public AgentUpgradeStepProvider getStepProvider() {
		return stepProvider;
	}

	public void setStepProvider(AgentUpgradeStepProvider stepProvider) {
		this.stepProvider = stepProvider;
	}
}

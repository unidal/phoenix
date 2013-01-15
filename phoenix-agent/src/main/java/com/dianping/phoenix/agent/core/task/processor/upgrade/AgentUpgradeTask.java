package com.dianping.phoenix.agent.core.task.processor.upgrade;

import com.dianping.phoenix.agent.core.task.AbstractTask;

public class AgentUpgradeTask extends AbstractTask {

	private String agentVersion;
	private String agentGitUrl;

	public AgentUpgradeTask(String agentVersion, String agentGitUrl) {
		this.agentVersion = agentVersion;
		this.agentGitUrl = agentGitUrl;
	}

	public String getAgentVersion() {
		return agentVersion;
	}

	public String getAgentGitUrl() {
		return agentGitUrl;
	}

}

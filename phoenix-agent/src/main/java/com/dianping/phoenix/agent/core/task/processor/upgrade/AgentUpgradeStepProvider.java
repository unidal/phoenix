package com.dianping.phoenix.agent.core.task.processor.upgrade;

import java.io.OutputStream;

public interface AgentUpgradeStepProvider {
	int prepare(AgentUpgradeTask task, OutputStream logOut, String underLyingFile);

	int init() throws Exception;

	int checkArgument() throws Exception;

	int gitPull() throws Exception;

	int dryrunAgent() throws Exception;

	int upgradeAgent() throws Exception;
}

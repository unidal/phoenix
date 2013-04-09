package com.dianping.phoenix.agent.core.task.processor.upgrade;

import com.dianping.phoenix.agent.core.task.workflow.Context;

public interface AgentUpgradeStepProvider {

	int init(Context ctx) throws Exception;

	int checkArgument(Context ctx) throws Exception;

	int gitPull(Context ctx) throws Exception;

	int dryrunAgent(Context ctx) throws Exception;

	int upgradeAgent(Context ctx) throws Exception;
}

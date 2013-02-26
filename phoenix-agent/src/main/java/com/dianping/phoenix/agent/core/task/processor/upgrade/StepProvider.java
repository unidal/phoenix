package com.dianping.phoenix.agent.core.task.processor.upgrade;

public interface StepProvider {

	int upgradeAgent(AgentUpgradeContext myCtx);

	int dryrunAgent(AgentUpgradeContext myCtx);

	int gitPull(AgentUpgradeContext myCtx);

}

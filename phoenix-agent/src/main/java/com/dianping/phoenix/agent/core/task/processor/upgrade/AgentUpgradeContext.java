package com.dianping.phoenix.agent.core.task.processor.upgrade;

import com.dianping.phoenix.agent.core.task.workflow.Context;

public class AgentUpgradeContext extends Context {
	
	private StepProvider stepProvider;
	
	public StepProvider getStepProvider() {
		return stepProvider;
	}

}

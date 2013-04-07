package com.dianping.phoenix.agent.core.task.processor.kernel.upgrade;

import com.dianping.phoenix.agent.core.task.processor.kernel.DeployTask;
import com.dianping.phoenix.agent.core.task.workflow.Context;

public class KernelUpgradeContext extends Context {
	private KernelUpgradeStepProvider stepProvider;
	private DeployTask task;

	public void setStepProvider(KernelUpgradeStepProvider stepProvider) {
		this.stepProvider = stepProvider;
	}

	public KernelUpgradeStepProvider getStepProvider() {
		return stepProvider;
	}

	public DeployTask getTask() {
		return task;
	}

	public void setTask(DeployTask task) {
		this.task = task;
	}

	@Override
	public boolean kill() {
		try {
			setKilled(true);
			stepProvider.kill();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}

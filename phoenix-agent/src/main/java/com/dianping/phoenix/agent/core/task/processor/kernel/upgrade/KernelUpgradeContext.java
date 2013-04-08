package com.dianping.phoenix.agent.core.task.processor.kernel.upgrade;

import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.shell.ScriptExecutor;
import com.dianping.phoenix.agent.core.task.workflow.Context;

public class KernelUpgradeContext extends Context {
	@Inject
	private ScriptExecutor scriptExecutor;
	@Inject
	private KernelUpgradeStepProvider stepProvider;

	public ScriptExecutor getScriptExecutor() {
		return scriptExecutor;
	}

	public KernelUpgradeStepProvider getStepProvider() {
		return stepProvider;
	}

	@Override
	public boolean kill() {
		try {
			setKilled(true);
			scriptExecutor.kill();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}

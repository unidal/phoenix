package com.dianping.phoenix.agent.core.task.workflow;

import java.util.HashMap;
import java.util.Map;

import com.dianping.phoenix.agent.core.task.processor.upgrade.AgentUpgradeStep;

public abstract class AbstractStep implements Step {

	private AgentUpgradeStep nextStepWhenSuccess;
	private AgentUpgradeStep nextStepWhenFail;
	private int stepSeq;

	protected AbstractStep(AgentUpgradeStep nextStepWhenSuccess, AgentUpgradeStep nextStepWhenFail, int stepSeq) {
		this.nextStepWhenSuccess = nextStepWhenSuccess;
		this.nextStepWhenFail = nextStepWhenFail;
		this.stepSeq = stepSeq;
	}

	@Override
	public Step getNextStep(int exitCode) {
		if (exitCode == CODE_ERROR) {
			return nextStepWhenFail;
		} else {
			return nextStepWhenSuccess;
		}
	}

	@Override
	public Map<String, String> getLogChunkHeader() {
		Map<String, String> header = new HashMap<String, String>();
		header.put(HEADER_STEP, toString());
		header.put(HEADER_PROGRESS, String.format("%s/%s", stepSeq, getTotalStep()));
		return header;
	}

	protected abstract int getTotalStep();

}

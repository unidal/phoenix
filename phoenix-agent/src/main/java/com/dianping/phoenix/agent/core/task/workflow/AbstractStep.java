package com.dianping.phoenix.agent.core.task.workflow;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractStep implements Step {

	private AbstractStep nextStepWhenSuccess;
	private AbstractStep nextStepWhenFail;
	private int stepSeq;

	protected AbstractStep(AbstractStep nextStepWhenSuccess, AbstractStep nextStepWhenFail, int stepSeq) {
		this.nextStepWhenSuccess = nextStepWhenSuccess;
		this.nextStepWhenFail = nextStepWhenFail;
		this.stepSeq = stepSeq;
	}

	@Override
	public Step getNextStep(int exitCode) {
		if (exitCode != CODE_OK) {
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

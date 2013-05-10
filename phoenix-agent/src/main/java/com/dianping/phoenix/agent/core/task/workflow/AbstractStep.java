package com.dianping.phoenix.agent.core.task.workflow;

import java.util.HashMap;
import java.util.Map;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;

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

	protected int doStepWithCat(Context ctx, String stepType, String stepName) {
		try {
			Cat.getManager().getThreadLocalMessageTree().setMessageId(ctx.getMsgId());
		} catch (Exception e) {
			// ignore it
		}
		Transaction t = Cat.getProducer().newTransaction(stepType, stepName);
		int stepCode = Step.CODE_ERROR;
		try {
			stepCode = doActivity(ctx);
			t.setStatus(stepCode == Step.CODE_OK ? Message.SUCCESS : STATUS_FAIL);
		} catch (Exception e) {
			t.setStatus(e);
		} finally {
			t.complete();
		}
		return stepCode;
	}

	protected abstract int getTotalStep();

	protected abstract int doActivity(Context ctx) throws Exception;
}

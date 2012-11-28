package com.dianping.phoenix.agent.core.task.processor.kernel;

import com.dianping.phoenix.agent.core.task.processor.AbstractSerialTaskProcessor;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.TransactionId;

public class DetachTaskProcessor extends AbstractSerialTaskProcessor<DetachTask> {

	@Override
	public boolean cancel(TransactionId txId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Class<DetachTask> handle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doProcess(Transaction tx) throws Exception {
		// TODO Auto-generated method stub
		
	}

}

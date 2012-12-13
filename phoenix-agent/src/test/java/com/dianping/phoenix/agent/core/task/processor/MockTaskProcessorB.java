package com.dianping.phoenix.agent.core.task.processor;

import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.processor.AbstractSerialTaskProcessor;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.Transaction.Status;
import com.dianping.phoenix.agent.core.tx.TransactionId;

public class MockTaskProcessorB extends AbstractSerialTaskProcessor<Task> {

	@Override
	public boolean cancel(TransactionId txId) {
		return false;
	}

	@Override
	public Class<Task> handle() {
		return null;
	}

	@Override
	protected Status doTransaction(Transaction tx) throws Exception {
		return null;
	}

}

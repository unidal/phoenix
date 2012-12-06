package com.dianping.phoenix.agent.core.task.processor;

import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.Transaction.Status;
import com.dianping.phoenix.agent.core.tx.TransactionId;

public class MockTaskProcessorA2 extends AbstractSerialTaskProcessor<Task> {

	private boolean throwException = false;

	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}

	@Override
	public boolean cancel(TransactionId txId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Class<Task> handle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Status doTransaction(Transaction tx) throws Exception {
		if (throwException) {
			throw new Exception("fake exception");
		} else {
			return Status.SUCCESS;
		}
	}

}

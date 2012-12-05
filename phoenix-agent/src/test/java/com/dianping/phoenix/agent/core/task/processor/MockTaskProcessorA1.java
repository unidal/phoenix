package com.dianping.phoenix.agent.core.task.processor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.processor.AbstractSerialTaskProcessor;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.Transaction.Status;
import com.dianping.phoenix.agent.core.tx.TransactionId;

public class MockTaskProcessorA1 extends AbstractSerialTaskProcessor<Task> {

	private CountDownLatch latch = new CountDownLatch(1);
	
	@Override
	public boolean cancel(TransactionId txId) {
		latch.countDown();
		return true;
	}

	@Override
	public Class<Task> handle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Status doTransaction(Transaction tx) throws Exception {
		boolean awaitOk = latch.await(2, TimeUnit.SECONDS);
		if(!awaitOk) {
			throw new RuntimeException("latch timeout");
		}
		return Status.SUCCESS;
	}

}

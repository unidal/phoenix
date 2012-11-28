package com.dianping.phoenix.agent.core.task.processor;

import java.util.List;

import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.TransactionId;

public interface TaskProcessor<T> {

	/**
	 * Try to submit transaction <code>tx<code> for processing
	 * @param tx
	 * @return whether the transaction is accepted for processing
	 * @throws Exception
	 */
	SubmitResult submit(Transaction tx) throws Exception;
	List<Transaction> currentTransactions();
	boolean cancel(TransactionId txId);
	Class<T> handle();
	boolean attachEventTracker(TransactionId txId, EventTracker eventTracker);
	
}

package com.dianping.phoenix.agent.core.task.processor;

import java.util.List;

import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.TransactionId;
import com.dianping.phoenix.agent.core.event.EventTracker;

public interface TaskProcessor<T> {

	void submit(Transaction tx) throws Exception;
	List<Transaction> currentTransactions();
	boolean cancel(TransactionId txId);
	Class<T> handle();
	boolean attachEventTracker(TransactionId txId, EventTracker eventTracker);
	
}

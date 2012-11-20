package com.dianping.phoenix.agent.core.task.processor;

import java.util.List;

import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.TransactionId;

public interface TaskProcessor {

	void submit(Transaction tx);
	List<Transaction> currentTransactions();
	boolean cancel(TransactionId txId);
	
}

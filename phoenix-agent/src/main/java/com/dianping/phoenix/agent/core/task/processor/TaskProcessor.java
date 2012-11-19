package com.dianping.phoenix.agent.core.task.processor;

import java.util.List;

import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.Transactional;

public interface TaskProcessor extends Transactional {

	void submit(Transaction tx);
	List<Transaction> currentTransactions();
}

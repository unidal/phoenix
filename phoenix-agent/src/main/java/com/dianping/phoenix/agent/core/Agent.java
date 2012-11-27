package com.dianping.phoenix.agent.core;

import java.io.IOException;
import java.io.Reader;

import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessor;
import com.dianping.phoenix.agent.core.tx.TransactionId;

public interface Agent extends TaskProcessor<Task> {

	Reader getLogReader(TransactionId txId, int offset) throws IOException;

	boolean isTransactionProcessing(TransactionId txId);
	
}

package com.dianping.phoenix.agent.core;

import java.io.Reader;

import com.dianping.phoenix.agent.core.task.processor.TaskProcessor;

public interface Agent extends Transactional, TaskProcessor {

	Reader getLog(TransactionId txId);
	
}

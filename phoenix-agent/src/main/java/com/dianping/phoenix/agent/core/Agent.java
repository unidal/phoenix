package com.dianping.phoenix.agent.core;

import java.io.IOException;
import java.io.Reader;

import com.dianping.phoenix.agent.core.task.processor.TaskProcessor;

public interface Agent extends TaskProcessor {

	Reader getLog(TransactionId txId, int offset) throws IOException;
	
}

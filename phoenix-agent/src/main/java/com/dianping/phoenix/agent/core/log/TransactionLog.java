package com.dianping.phoenix.agent.core.log;

import java.io.Reader;

import com.dianping.phoenix.agent.core.TransactionId;

public interface TransactionLog {

	void log(TransactionId txId, String log);
	void close(TransactionId txId);
	Reader getLog(TransactionId txId);
	
}

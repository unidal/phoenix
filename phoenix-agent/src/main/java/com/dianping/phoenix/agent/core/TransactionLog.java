package com.dianping.phoenix.agent.core;

import java.io.InputStream;

public interface TransactionLog {

	void log(long txId, String log);
	void close(long txId);
	InputStream getLog(long txId);
	
}

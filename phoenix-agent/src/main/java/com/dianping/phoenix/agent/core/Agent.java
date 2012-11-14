package com.dianping.phoenix.agent.core;

import java.io.InputStream;

public interface Agent {

	public enum Status {
		INIT, STARTED, PROCESSING, DONE, COMMIT, ROLLBACK
	}
	
	void startTransaction(long txId, String desc, EventTracker tracker) throws IllegalStateException;
	void process(Task task);
	void commit();
	void rollback();
	InputStream getLog(long txId);
}

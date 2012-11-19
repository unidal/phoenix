package com.dianping.phoenix.agent.core;

public interface Transactional {
	
	void commit(TransactionId txId);
	
	void rollback(TransactionId txId);
	
}

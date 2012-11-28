package com.dianping.phoenix.agent.core.event;

import com.dianping.phoenix.agent.core.tx.TransactionId;

public interface Event {

	String getMsg();
	TransactionId getTransactionId();
	
}

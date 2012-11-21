package com.dianping.phoenix.agent.core.event;

import com.dianping.phoenix.agent.core.TransactionId;

public interface Event {

	String getMsg();
	TransactionId getTransactionId();
	
}

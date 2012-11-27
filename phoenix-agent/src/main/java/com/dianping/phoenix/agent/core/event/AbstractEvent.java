package com.dianping.phoenix.agent.core.event;

import com.dianping.phoenix.agent.core.tx.TransactionId;

public abstract class AbstractEvent implements Event {

	private String msg;
	private TransactionId txId;

	public AbstractEvent(TransactionId txId, String msg) {
		this.txId = txId;
		this.msg = msg;
	}

	@Override
	public String getMsg() {
		return msg;
	}

	@Override
	public TransactionId getTransactionId() {
		return txId;
	}

}

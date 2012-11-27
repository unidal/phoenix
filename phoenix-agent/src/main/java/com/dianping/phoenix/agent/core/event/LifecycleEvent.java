package com.dianping.phoenix.agent.core.event;

import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.TransactionId;

public class LifecycleEvent extends AbstractEvent {

	private Transaction.Status status;

	public LifecycleEvent(TransactionId txId, String msg, Transaction.Status status) {
		super(txId, msg);
		this.status = status;
	}

	public Transaction.Status getStatus() {
		return status;
	}

}

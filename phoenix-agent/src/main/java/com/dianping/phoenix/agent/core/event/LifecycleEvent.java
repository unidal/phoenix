package com.dianping.phoenix.agent.core.event;

import com.dianping.phoenix.agent.core.TransactionId;
import com.dianping.phoenix.agent.core.task.Task.Status;

public class LifecycleEvent extends AbstractEvent {

	private Status status;

	public LifecycleEvent(TransactionId txId, String msg, Status status) {
		super(txId, msg);
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

}

package com.dianping.phoenix.agent.core;

import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.task.Task;

public class Transaction {

	private Task task;
	private TransactionId txId;
	private EventTracker eventTracker;

	public Transaction(Task task, TransactionId txId, EventTracker eventTracker) {
		this.task = task;
		this.txId = txId;
		this.eventTracker = eventTracker;
	}
	
	public Task getTask() {
		return task;
	}

	public TransactionId getTxId() {
		return txId;
	}

	public EventTracker getEventTracker() {
		return eventTracker;
	}

	public void setEventTracker(EventTracker eventTracker) {
		this.eventTracker = eventTracker;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((txId == null) ? 0 : txId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transaction other = (Transaction) obj;
		if (txId == null) {
			if (other.txId != null)
				return false;
		} else if (!txId.equals(other.txId))
			return false;
		return true;
	}

}

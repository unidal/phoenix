package com.dianping.phoenix.agent.core.tx;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.task.Task;

public class Transaction {

	public static enum Status {
		INIT, REJECTED, PROCESSING, FAILED, SUCCESS;

		private final static Set<Status> COMPLETED_STATUS_SET = new HashSet<Transaction.Status>();
		static {
			COMPLETED_STATUS_SET.add(REJECTED);
			COMPLETED_STATUS_SET.add(FAILED);
			COMPLETED_STATUS_SET.add(SUCCESS);
		};

		/**
		 * 是否是终结状态
		 * 
		 * @return
		 */
		public boolean isCompleted() {
			return COMPLETED_STATUS_SET.contains(this);
		}

	}

	@JsonIgnore
	private Task task;
	private TransactionId txId;
	@JsonIgnore
	private EventTracker eventTracker;
	private Status status;

	public Transaction(Task task, TransactionId txId, EventTracker eventTracker) {
		this.task = task;
		this.txId = txId;
		this.eventTracker = eventTracker;
		status = Status.INIT;
	}

	/**
	 * for serialization
	 */
	@SuppressWarnings("unused")
	private Transaction() {
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

	public Status getStatus() {
		return status;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public void setStatus(Status status) {
		if(status == null) {
			throw new IllegalStateException("status of transaction can't be set to null");
		}
		this.status = status;
	}

	@Override
	public String toString() {
		ReflectionToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);
		return ReflectionToStringBuilder.toStringExclude(this, "eventTracker");
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

package com.dianping.phoenix.agent.core.tx;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class TransactionId {

	private long id;

	public TransactionId(long id) {
		this.id = id;
	}
	
	/** 
	 * for serialization
	 */
	@SuppressWarnings("unused")
	private TransactionId() {
	}

	public long getId() {
		return id;
	}
	
	public String toReadableFormat() {
		return Long.toString(id);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		TransactionId other = (TransactionId) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
}

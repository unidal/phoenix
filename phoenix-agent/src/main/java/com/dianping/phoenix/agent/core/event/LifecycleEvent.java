package com.dianping.phoenix.agent.core.event;

import com.dianping.phoenix.agent.core.task.Task.Status;

public class LifecycleEvent extends AbstractEvent {

	private Status status;

	public LifecycleEvent(String msg, Status status) {
		super(msg);
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

}

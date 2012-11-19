package com.dianping.phoenix.agent.core.task;


public abstract class AbstractTask implements Task {

	protected Status status;

	@Override
	public Status getStatus() {
		return status;
	}

}

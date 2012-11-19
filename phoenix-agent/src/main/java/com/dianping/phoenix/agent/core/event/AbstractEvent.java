package com.dianping.phoenix.agent.core.event;

public abstract class AbstractEvent implements Event {

	private String msg;

	public AbstractEvent(String msg) {
		this.msg = msg;
	}

	@Override
	public String getMsg() {
		return msg;
	}

}

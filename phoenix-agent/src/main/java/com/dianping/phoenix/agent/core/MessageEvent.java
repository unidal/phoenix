package com.dianping.phoenix.agent.core;

public class MessageEvent implements Event {

	private String msg;

	public MessageEvent(String msg) {
		this.msg = msg;
	}

	@Override
	public String getMsg() {
		return msg;
	}

}

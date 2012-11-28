package com.dianping.phoenix.agent.core.task.processor;

public class SubmitResult {

	private boolean accepted;
	private String msg;

	public SubmitResult(boolean accepted, String msg) {
		this.accepted = accepted;
		this.msg = msg;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}

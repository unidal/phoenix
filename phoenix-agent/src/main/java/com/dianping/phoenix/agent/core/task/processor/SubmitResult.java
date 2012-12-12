package com.dianping.phoenix.agent.core.task.processor;

public class SubmitResult {

	public enum REASON {
		DUPLICATE_TXID, ANOTHER_TX_RUNNING
	}

	private boolean accepted;
	private REASON reason;

	public SubmitResult(boolean accepted) {
		this.accepted = accepted;
	}

	public SubmitResult(boolean accepted, REASON reason) {
		this.accepted = accepted;
		this.reason = reason;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public REASON getReason() {
		return reason;
	}

	public void setReason(REASON reason) {
		this.reason = reason;
	}

}

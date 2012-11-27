package com.dianping.phoenix.agent.core.event;

import com.dianping.phoenix.agent.core.tx.TransactionId;

public class MessageEvent extends AbstractEvent {

	public MessageEvent(TransactionId txId, String msg) {
		super(txId, msg);
	}

}

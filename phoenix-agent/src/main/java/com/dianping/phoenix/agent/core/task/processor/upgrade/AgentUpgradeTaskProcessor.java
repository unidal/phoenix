package com.dianping.phoenix.agent.core.task.processor.upgrade;

import com.dianping.phoenix.agent.core.task.processor.AbstractSerialTaskProcessor;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.Transaction.Status;
import com.dianping.phoenix.agent.core.tx.TransactionId;

public class AgentUpgradeTaskProcessor extends AbstractSerialTaskProcessor<AgentUpgradeTask> {

	@Override
	public boolean cancel(TransactionId txId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Class<AgentUpgradeTask> handle() {
		return AgentUpgradeTask.class;
	}

	@Override
	protected Status doTransaction(Transaction tx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}

package com.dianping.phoenix.agent.core.task.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.TransactionId;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.task.Task.Status;

public abstract class AbstractSerialTaskProcessor implements TaskProcessor {

	private Map<TransactionId, Transaction> txId2Tx = new ConcurrentHashMap<TransactionId, Transaction>();

	protected abstract void doProcess(Transaction tx);
	
	@Override
	public void submit(Transaction tx) {

		EventTracker eventTracker = tx.getEventTracker();
		if (!idle()) {
			eventTracker.onEvent(new LifecycleEvent("some task is running", Status.REJECTED));
			return;
		}
		registerTransaction(tx);
		
		doProcess(tx);
		
		if (tx.isAutoCommit()) {
			commit(tx.getTxId());
		}
	}

	private boolean idle() {
		return txId2Tx.isEmpty();
	}

	private void registerTransaction(Transaction tx) {
		txId2Tx.put(tx.getTxId(), tx);
	}

	private void deRegisterTask(TransactionId tx) {
		txId2Tx.remove(tx);
	}

	@Override
	public void rollback(TransactionId txId) {
		deRegisterTask(txId);
	}

	@Override
	public void commit(TransactionId txId) {
		deRegisterTask(txId);
	}

	@Override
	public List<Transaction> currentTransactions() {
		return new ArrayList<Transaction>(txId2Tx.values());
	}
	
}

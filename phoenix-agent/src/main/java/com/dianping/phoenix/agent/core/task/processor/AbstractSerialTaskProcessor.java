package com.dianping.phoenix.agent.core.task.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.TransactionId;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.task.Task.Status;

public abstract class AbstractSerialTaskProcessor implements TaskProcessor {

	private Map<TransactionId, Transaction> txId2Tx = new ConcurrentHashMap<TransactionId, Transaction>();
	private Lock lock = new ReentrantLock();

	protected abstract void doProcess(Transaction tx);

	@Override
	public void submit(Transaction tx) {
		if (lock.tryLock()) {
			try {
				registerTransaction(tx);

				try {
					doProcess(tx);
				} catch (Exception e) {
					// TODO: handle exception
				}

				deRegisterTask(tx.getTxId());
			} catch (Exception e) {
				// TODO: handle exception
			} finally {
				lock.unlock();
			}
		} else {
			tx.getEventTracker().onEvent(new LifecycleEvent("some task is running", Status.REJECTED));
		}

	}

	private void registerTransaction(Transaction tx) {
		txId2Tx.put(tx.getTxId(), tx);
	}

	private void deRegisterTask(TransactionId tx) {
		txId2Tx.remove(tx);
	}

	@Override
	public List<Transaction> currentTransactions() {
		return new ArrayList<Transaction>(txId2Tx.values());
	}

}

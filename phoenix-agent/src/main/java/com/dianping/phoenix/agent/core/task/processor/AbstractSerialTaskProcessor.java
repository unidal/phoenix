package com.dianping.phoenix.agent.core.task.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.TransactionId;
import com.dianping.phoenix.agent.core.event.AbstractEventTracker;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.EventTrackerChain;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.task.Task.Status;

public abstract class AbstractSerialTaskProcessor<T> implements TaskProcessor<T> {

	protected Transaction currentTx;
	private Semaphore semaphore = new Semaphore(1); 
	protected EventTrackerChain eventTrackerChain;

	/**
	 * Remember to call transactionEnd when task is done!!!
	 * 
	 * @param tx
	 * @throws Exception
	 */
	protected abstract void doProcess(Transaction tx) throws Exception;

	private synchronized void initEventTrackerChain(EventTracker eventTracker) {
		if (!(eventTracker instanceof EventTrackerChain)) {
			eventTrackerChain = new EventTrackerChain(eventTracker);
		} else {
			eventTrackerChain = (EventTrackerChain) eventTracker;
		}
		eventTrackerChain.add(new AbstractEventTracker() {

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				if(event.getStatus().isCompleted()) {
					transactionEnd(event.getTransactionId());
				}
			}
			
		});
	}

	@Override
	public void submit(Transaction tx) {
		if (semaphore.tryAcquire()) {
			try {
				initEventTrackerChain(tx.getEventTracker());
				transactionStart(tx);
			} catch (Exception e) {
				transactionEnd(tx.getTxId());
				throw new RuntimeException(e);
			}
			
			try {
				doProcess(tx);
			} catch (Exception e) {
				//TODO
			}
		} else {
			tx.getEventTracker().onEvent(new LifecycleEvent(tx.getTxId(), "some task is running", Status.REJECTED));
		}

	}

	private void transactionStart(Transaction tx) {
		currentTx = tx;
	}

	private synchronized void transactionEnd(TransactionId txId) {
		if(currentTx != null && !currentTx.getTxId().equals(txId)) {
			throw new IllegalStateException(String.format("current transaction is %s while try to end %s", currentTx.getTxId(), txId));
		}
		currentTx = null;
		if(semaphore.availablePermits() == 0) {
			semaphore.release();
		}
	}

	@Override
	public synchronized List<Transaction> currentTransactions() {
		if (currentTx != null) {
			List<Transaction> currentTxes = new ArrayList<Transaction>(1);
			currentTxes.add(currentTx);
			return currentTxes;
		} else {
			return Collections.emptyList();
		}
	}
	
	@Override
	public synchronized boolean attachEventTracker(TransactionId txId, EventTracker eventTracker) {
		if (currentTx != null && currentTx.getTxId().equals(txId)) {
			eventTrackerChain.add(eventTracker);
			return true;
		} else {
			return false;
		}
	}

}

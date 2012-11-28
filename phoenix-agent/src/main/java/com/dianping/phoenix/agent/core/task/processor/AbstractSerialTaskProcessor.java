package com.dianping.phoenix.agent.core.task.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.EventTrackerChain;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.TransactionId;
import com.site.helper.Threads;

public abstract class AbstractSerialTaskProcessor<T> implements TaskProcessor<T> {

	protected Transaction currentTx;
	private Semaphore semaphore = new Semaphore(1);
	protected EventTrackerChain eventTrackerChain;

	protected abstract void doProcess(Transaction tx) throws Exception;

	private synchronized void initEventTrackerChain(EventTracker eventTracker) {
		if (!(eventTracker instanceof EventTrackerChain)) {
			eventTrackerChain = new EventTrackerChain(eventTracker);
		} else {
			eventTrackerChain = (EventTrackerChain) eventTracker;
		}
	}

	@Override
	public SubmitResult submit(final Transaction tx) {
		Logger logger = getLogger();
		SubmitResult submitResult = new SubmitResult(false, "");
		if (semaphore.tryAcquire()) {
			logger.info("accept " + tx);
			
			final Class<?> clazz = getClass();
			Threads.Task task = new Threads.Task() {

				@Override
				public void run() {
					try {
						initEventTrackerChain(tx.getEventTracker());
						transactionStart(tx);
						doProcess(tx);
					} catch (Exception e) {
						throw new RuntimeException(e);
					} finally {
						semaphore.release();
						transactionEnd(tx.getTxId());
					}
				}

				@Override
				public void shutdown() {

				}

				@Override
				public String getName() {
					return clazz.getSimpleName();
				}
			};
			Threads.forGroup("Phoenix").start(task);
			submitResult.setAccepted(true);
		} else {
			logger.info("reject " + tx);
			submitResult.setAccepted(false);
			submitResult.setMsg("another transaction is running");
		}
		return submitResult;
	}

	private Logger getLogger() {
		Logger logger = Logger.getLogger(getClass());
		return logger;
	}

	private synchronized void transactionStart(Transaction tx) {
		currentTx = tx;
	}

	private synchronized void transactionEnd(TransactionId txId) {
		if (currentTx != null && currentTx.getTxId().equals(txId)) {
			currentTx = null;
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

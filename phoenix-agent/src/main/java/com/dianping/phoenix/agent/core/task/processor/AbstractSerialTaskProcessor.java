package com.dianping.phoenix.agent.core.task.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.unidal.helper.Threads;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.EventTrackerChain;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.Transaction.Status;
import com.dianping.phoenix.agent.core.tx.TransactionId;
import com.dianping.phoenix.agent.core.tx.TransactionManager;

public abstract class AbstractSerialTaskProcessor<T> implements TaskProcessor<T> {

	@Inject
	protected TransactionManager txMgr;
	@Inject
	private SemaphoreWrapper semaphoreWrapper;

	protected Transaction currentTx;
	protected EventTrackerChain eventTrackerChain;

	protected abstract Status doTransaction(Transaction tx) throws Exception;

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
		if (semaphoreWrapper.getSemaphore().tryAcquire()) {
			logger.info("accept " + tx);

			final Class<?> clazz = getClass();
			Threads.Task task = new Threads.Task() {

				@Override
				public void run() {
					String eventMsg = "ok";
					try {
						startTransaction(tx);
						try {
							tx.setStatus(doTransaction(tx));
						} catch (Exception e) {
							tx.setStatus(Status.FAILED);
							eventMsg = e.getMessage();
						} finally {
							txMgr.saveTransaction(tx);
							getLogger().info("end processing " + tx);
						}
					} catch (Exception e) {
						getLogger().error("error preparing transaction", e);
						throw new RuntimeException(e);
					} finally {
						semaphoreWrapper.getSemaphore().release();
						endTransaction(tx.getTxId());
						eventTrackerChain.onEvent(new LifecycleEvent(tx.getTxId(), eventMsg, tx.getStatus()));
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
			Threads.forGroup("Phoenix").start(task, false);
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

	private synchronized void startTransaction(Transaction tx) throws IOException {
		getLogger().info("start processing " + tx);
		currentTx = tx;
		initEventTrackerChain(tx.getEventTracker());
		tx.setStatus(Status.PROCESSING);
		txMgr.saveTransaction(tx);
	}

	private synchronized void endTransaction(TransactionId txId) {
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

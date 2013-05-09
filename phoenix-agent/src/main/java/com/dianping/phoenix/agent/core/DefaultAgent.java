package com.dianping.phoenix.agent.core;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.event.AbstractEventTracker;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.EventTrackerChain;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.processor.SubmitResult;
import com.dianping.phoenix.agent.core.task.processor.SubmitResult.REASON;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessor;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessorFactory;
import com.dianping.phoenix.agent.core.tx.Log4jAppender;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.TransactionId;
import com.dianping.phoenix.agent.core.tx.TransactionManager;

public class DefaultAgent implements Agent {

	private final static Logger logger = Logger.getLogger(DefaultAgent.class);

	@Inject
	private TransactionManager txMgr;
	@Inject
	private TaskProcessorFactory taskProcessorFactory;

	private Map<TransactionId, TaskProcessor<Task>> txId2Processor;

	public DefaultAgent() {
		txId2Processor = new ConcurrentHashMap<TransactionId, TaskProcessor<Task>>();
	}

	@Override
	public SubmitResult submit(Transaction tx) throws Exception {
		logger.info("try to submit " + tx);
		SubmitResult submitResult = new SubmitResult(false);
		final TransactionId txId = tx.getTxId();

		if (!txMgr.startTransaction(txId)) {
			logger.warn(String.format("transaction with same id exists, reject it %s", txId));
			submitResult.setAccepted(false);
			submitResult.setReason(REASON.DUPLICATE_TXID);
		} else {
			TaskProcessor<Task> taskProcessor = taskProcessorFactory.findTaskProcessor(tx.getTask());
			if (taskProcessor != null) {
				addCleanupEventTracker(tx, txId);
				txId2Processor.put(txId, taskProcessor);
				submitResult = taskProcessor.submit(tx);
			} else {
				logger.error(String.format("no task processor found for %s", tx.getTask()));
			}
		}

		return submitResult;
	}

	private void addCleanupEventTracker(Transaction tx, final TransactionId txId) {
		EventTrackerChain eventTrackerChain = new EventTrackerChain();

		eventTrackerChain.add(new AbstractEventTracker() {

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				if (event.getStatus().isCompleted()) {
					Log4jAppender.endTeeLog();
					txId2Processor.remove(txId);
					txMgr.endTransaction(txId);
				}
			}

		});
		eventTrackerChain.add(tx.getEventTracker());

		tx.setEventTracker(eventTrackerChain);
	}

	@Override
	public Reader getLogReader(TransactionId txId, int offset) throws IOException {
		return txMgr.getLogReader(txId, offset);
	}

	@Override
	public List<Transaction> currentTransactions() {
		return taskProcessorFactory.currentTransactions();
	}

	@Override
	public boolean cancel(TransactionId txId) {
		TaskProcessor<Task> processor = txId2Processor.get(txId);
		if (processor != null) {
			return processor.cancel(txId);
		}
		return true;
	}

	@Override
	public Class<Task> handle() {
		return Task.class;
	}

	@Override
	public boolean attachEventTracker(TransactionId txId, EventTracker eventTracker) {
		TaskProcessor<Task> taskProcessor = txId2Processor.get(txId);
		if (taskProcessor != null) {
			return taskProcessor.attachEventTracker(txId, eventTracker);
		} else {
			return false;
		}
	}

	@Override
	public boolean isTransactionProcessing(TransactionId txId) {
		return txId2Processor.containsKey(txId);
	}
}

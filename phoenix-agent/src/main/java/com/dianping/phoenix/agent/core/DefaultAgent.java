package com.dianping.phoenix.agent.core;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.phoenix.agent.core.event.Event;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.EventTrackerChain;
import com.dianping.phoenix.agent.core.event.MessageEvent;
import com.dianping.phoenix.agent.core.log.InMemoryTransactionLog;
import com.dianping.phoenix.agent.core.log.TransactionLog;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessor;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessorFactory;

public class DefaultAgent implements Agent {

	private EventTrackerChain eventTrackerChain;
	private TransactionLog txLog;
	private Map<TransactionId, TaskProcessor> txId2Processor;

	public DefaultAgent() {
		txLog = new InMemoryTransactionLog();
		txId2Processor = new ConcurrentHashMap<TransactionId, TaskProcessor>();
	}

	@Override
	public void submit(Transaction tx) {
		final TransactionId txId = tx.getTxId();
		eventTrackerChain = new EventTrackerChain();
		eventTrackerChain.add(new EventTracker() {

			@Override
			public void onEvent(Event event) {
				txLog.log(txId, event.getMsg());
			}
		});
		eventTrackerChain.add(tx.getEventTracker());
		
		TaskProcessor taskProcessor = TaskProcessorFactory.getInstance().findTaskProcessor(tx.getTask());
		txId2Processor.put(txId, taskProcessor);
		tx.setEventTracker(eventTrackerChain);
		taskProcessor.submit(tx);
	}

	@Override
	public void commit(TransactionId txId) {
		eventTrackerChain.onEvent(new MessageEvent("commit " + txId));
		TaskProcessor taskProcessor = txId2Processor.get(txId);
		if(taskProcessor == null) {
			// TODO: 
		} else {
			taskProcessor.commit(txId);
		}
	}

	@Override
	public void rollback(TransactionId txId) {
		eventTrackerChain.onEvent(new MessageEvent("rollback " + txId));
		TaskProcessor taskProcessor = txId2Processor.get(txId);
		if(taskProcessor == null) {
			// TODO: 
		} else {
			taskProcessor.rollback(txId);
		}
	}

	@Override
	public Reader getLog(TransactionId txId) {
		return txLog.getLog(txId);
	}

	@Override
	public List<Transaction> currentTransactions() {
		return TaskProcessorFactory.getInstance().currentTransactions();
	}

}

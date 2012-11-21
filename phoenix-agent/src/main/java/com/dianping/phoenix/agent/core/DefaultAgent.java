package com.dianping.phoenix.agent.core;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.event.AbstractEventTracker;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.EventTrackerChain;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.log.TransactionLog;
import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessor;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessorFactory;

public class DefaultAgent implements Agent {

	@Inject
	private TransactionLog txLog;
	@Inject
	private TaskProcessorFactory taskProcessorFactory;
	private Map<TransactionId, TaskProcessor<Task>> txId2Processor;

	public DefaultAgent() {
		txId2Processor = new ConcurrentHashMap<TransactionId, TaskProcessor<Task>>();
	}

	@Override
	public void submit(Transaction tx) throws Exception {
		final TransactionId txId = tx.getTxId();
		EventTrackerChain eventTrackerChain = new EventTrackerChain();
		eventTrackerChain.add(new AbstractEventTracker() {

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				if(event.getStatus().isCompleted()) {
					txId2Processor.remove(txId);
				}
			}

		});
		eventTrackerChain.add(tx.getEventTracker());
		
		TaskProcessor<Task> taskProcessor = taskProcessorFactory.findTaskProcessor(tx.getTask());
		txId2Processor.put(txId, taskProcessor);
		tx.setEventTracker(eventTrackerChain);
		taskProcessor.submit(tx);
	}

	@Override
	public Reader getLog(TransactionId txId, int offset) throws IOException {
		return txLog.getLog(txId, offset);
	}

	@Override
	public List<Transaction> currentTransactions() {
		return taskProcessorFactory.currentTransactions();
	}

	@Override
	public boolean cancel(TransactionId txId) {
		TaskProcessor<Task> processor = txId2Processor.get(txId);
		if(processor != null) {
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
		if(taskProcessor != null) {
			return taskProcessor.attachEventTracker(txId, eventTracker);
		} else {
			return false;
		}
	}

}

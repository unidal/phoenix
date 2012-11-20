package com.dianping.phoenix.agent.core;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.phoenix.agent.core.event.Event;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.EventTrackerChain;
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
				try {
					txLog.getWriter(txId).write(event.getMsg());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		eventTrackerChain.add(tx.getEventTracker());
		
		TaskProcessor taskProcessor = TaskProcessorFactory.getInstance().findTaskProcessor(tx.getTask());
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
		return TaskProcessorFactory.getInstance().currentTransactions();
	}

	@Override
	public boolean cancel(TransactionId txId) {
		TaskProcessor processor = txId2Processor.get(txId);
		if(processor != null) {
			return processor.cancel(txId);
		}
		return true;
	}

}

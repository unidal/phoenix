package com.dianping.phoenix.agent.core;

import java.io.InputStream;

public class DefaultAgent implements Agent {

	private Status status = Status.INIT;
	private EventTrackerChain eventTrackerChain;
	private TransactionLog txLog;
	private long currentTxid;

	@Override
	public void startTransaction(long txId, String desc, EventTracker eventTracker) throws IllegalStateException {
		if (Status.INIT != this.status) {
			throw new IllegalStateException();
		}
		eventTrackerChain = new EventTrackerChain();
		eventTrackerChain.add(new EventTracker() {

			@Override
			public void onEvent(Event event) {
				txLog.log(currentTxid, event.getMsg());
			}
		});
		eventTrackerChain.add(eventTracker);
		currentTxid = txId;
		txLog = new InMemoryTransactionLog();
		updateStatus(Status.STARTED);
	}

	@Override
	public void process(Task task) {
		eventTrackerChain.onEvent(new MessageEvent("start processing " + currentTxid));
		updateStatus(Status.PROCESSING);
		TaskProcessorFactory.getInstance().findTaskProcessor(task).process(task, eventTrackerChain);
		eventTrackerChain.onEvent(new MessageEvent("processing done " + currentTxid));
		updateStatus(Status.DONE);
	}

	@Override
	public void commit() {
		updateStatus(Status.COMMIT);
		updateStatus(Status.INIT);
	}

	@Override
	public void rollback() {
		updateStatus(Status.ROLLBACK);
		updateStatus(Status.INIT);
	}

	private void updateStatus(final Status newStatus) {
		this.status = newStatus;
	}

	@Override
	public InputStream getLog(long txId) {
		return txLog.getLog(txId);
	}

}

package com.dianping.phoenix.agent.core.task.processor.shell;

import java.util.List;
import java.util.Random;

import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.task.processor.SubmitResult;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessor;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.TransactionId;

public class ShellCmdTaskProcessor implements TaskProcessor<ShellCmdTask> {

	@Override
	public SubmitResult submit(Transaction tx) {
		ShellCmdTask shellCmdTask = (ShellCmdTask) tx.getTask();
		Random rnd = new Random();
		Transaction.Status status = Transaction.Status.FAILED;
		System.out.println("Shell running " + shellCmdTask.getCmd());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		if(rnd.nextBoolean()) {
			status = Transaction.Status.SUCCESS;
		}
		tx.getEventTracker().onEvent(new LifecycleEvent(tx.getTxId(), "success", status));
		return new SubmitResult(true);
	}

	@Override
	public List<Transaction> currentTransactions() {
		return null;
	}

	@Override
	public boolean cancel(TransactionId txId) {
		return false;
	}

	@Override
	public Class<ShellCmdTask> handle() {
		return ShellCmdTask.class;
	}

	@Override
	public boolean attachEventTracker(TransactionId txId, EventTracker eventTracker) {
		return false;
	}

}

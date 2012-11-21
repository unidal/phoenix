package com.dianping.phoenix.agent.core.task.processor.shell;

import java.util.List;
import java.util.Random;

import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.TransactionId;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.task.Task.Status;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessor;

public class ShellCmdTaskProcessor implements TaskProcessor<ShellCmdTask> {

	@Override
	public void submit(Transaction tx) {
		ShellCmdTask shellCmdTask = (ShellCmdTask) tx.getTask();
		Random rnd = new Random();
		Status status = Status.FAILED;
		System.out.println("Shell running " + shellCmdTask.getCmd());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		if(rnd.nextBoolean()) {
			status = Status.SUCCESS;
		}
		tx.getEventTracker().onEvent(new LifecycleEvent(tx.getTxId(), "success", status));
	}

	@Override
	public List<Transaction> currentTransactions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean cancel(TransactionId txId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Class<ShellCmdTask> handle() {
		return ShellCmdTask.class;
	}

	@Override
	public boolean attachEventTracker(TransactionId txId, EventTracker eventTracker) {
		// TODO Auto-generated method stub
		return false;
	}

}

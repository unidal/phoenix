package com.dianping.phoenix.agent.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.agent.core.event.AbstractEventTracker;
import com.dianping.phoenix.agent.core.event.Event;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.event.MessageEvent;
import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.Task.Status;
import com.dianping.phoenix.agent.core.task.processor.shell.ShellCmdTask;
import com.dianping.phoenix.agent.core.task.processor.war.Artifact;
import com.dianping.phoenix.agent.core.task.processor.war.WarUpdateTask;


public class ConsoleTest extends ComponentTestCase {
	
	private long id = 1L;
	
	private TransactionId generateTxId() {
		return new TransactionId(id++);
	}

	@Test
	public void testAgent() throws Exception {
		final Agent agent = new DefaultAgent();
		
		Thread t = new Thread() {

			@Override
			public void run() {
				while(true) {
					for(Transaction tx : agent.currentTransactions()) {
						System.out.println("currentTx: " + tx );
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
			}
			
		};
		t.setDaemon(true);
		t.start();
		
		final AtomicBoolean taskSuccess = new AtomicBoolean(false);
		EventTracker eventTracker = new AbstractEventTracker() {

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				LifecycleEvent levent = (LifecycleEvent)event;
				if(levent.getStatus() == Status.SUCCESS) {
					taskSuccess.set(true);
				} else {
					taskSuccess.set(false);
				}
				System.out.println(levent.getStatus());
			}

			@Override
			protected void onOtherEvent(Event event) {
			}

			@Override
			protected void onMessageEvent(MessageEvent event) {
				System.out.println(event.getMsg());
			}
		};
		
//		lookup(TaskProcessorFactory.class).registerTaskProcessor(ShellCmdTask.class, new ShellCmdTaskProcessor());
		
		List<Task> tasks = new ArrayList<Task>();
		
		Artifact artifactToUpdate = new Artifact("user-web", "1.0");
		tasks.add(new WarUpdateTask(artifactToUpdate, "1.1"));
		
		tasks.add(new ShellCmdTask("ps ax"));
		
		for (Task task : tasks) {
			Transaction tx = new Transaction(task, generateTxId(), eventTracker);
			agent.submit(tx);
		}
		
	}
}

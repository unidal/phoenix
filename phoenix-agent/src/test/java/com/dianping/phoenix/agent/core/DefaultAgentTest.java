package com.dianping.phoenix.agent.core;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.agent.core.event.Event;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessor;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessorFactory;
import com.dianping.phoenix.agent.core.task.processor.kernel.DeployTask;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.TransactionId;
import com.dianping.phoenix.agent.core.tx.Transaction.Status;

public class DefaultAgentTest extends ComponentTestCase {

	private Agent agent;

	@Before
	public void before() {
		agent = new DefaultAgent();
	}

	@Test
	public void testEventTracker() throws Exception {
		final AtomicBoolean trackerCalled = new AtomicBoolean(false);
		EventTracker eventTracker = new EventTracker() {

			@Override
			public void onEvent(Event event) {
				trackerCalled.set(true);
			}
		};
		Transaction tx = new Transaction(mock(DeployTask.class), new TransactionId(1L), eventTracker);
		agent.submit(tx);
		Assert.assertTrue(trackerCalled.get());
	}

	@Test
	public void testTransactionLog() throws Exception {
		Task task = mock(DeployTask.class);
		TransactionId txId = new TransactionId(1L);
		EventTracker eventTracker = new EventTracker() {

			@Override
			public void onEvent(Event event) {
			}
		};
		Transaction tx = new Transaction(task, txId, eventTracker);
		agent.submit(tx);
		char[] buf = new char[4096];
		agent.getLogReader(txId, 0).read(buf);
		Assert.assertTrue(buf.length > 0);
	}

	@Test
	public void testTrackLifecycleEvent() throws Exception {
		Task task = mock(Task.class);
		final TransactionId txId = new TransactionId(1L);
		Transaction tx = new Transaction(task, txId, null);
		TaskProcessor<Task> taskProcessor = mock(TaskProcessor.class);
		final List<Transaction.Status> statusHolder = new ArrayList<Transaction.Status>();
		doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Transaction tx = (Transaction) invocation.getArguments()[0];
				tx.getEventTracker().onEvent(new LifecycleEvent(txId, "", statusHolder.get(0)));
				return null;
			}
		}).when(taskProcessor).submit(any(Transaction.class));
		lookup(TaskProcessorFactory.class).registerTaskProcessor(task.getClass(), taskProcessor);

		for (final Transaction.Status status : Transaction.Status.values()) {
			final AtomicBoolean statusRecieved = new AtomicBoolean(false);
			EventTracker eventTracker = new EventTracker() {

				@Override
				public void onEvent(Event event) {
					if (event instanceof LifecycleEvent && ((LifecycleEvent) event).getStatus() == status) {
						statusRecieved.set(true);
					}
				}
			};
			tx.setEventTracker(eventTracker);
			statusHolder.clear();
			statusHolder.add(status);
			agent.submit(tx);
			Assert.assertTrue(statusRecieved.get());
		}

	}

}

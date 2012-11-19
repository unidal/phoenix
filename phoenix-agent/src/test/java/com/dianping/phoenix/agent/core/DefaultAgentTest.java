package com.dianping.phoenix.agent.core;

import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.dianping.phoenix.agent.core.event.Event;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.Task.Status;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessor;
import com.dianping.phoenix.agent.core.task.processor.TaskProcessorFactory;
import com.dianping.phoenix.agent.core.task.processor.war.WarUpdateTask;

public class DefaultAgentTest {

	private Agent agent;

	@Before
	public void before() {
		agent = new DefaultAgent();
	}

	@Test
	public void testEventTracker() {
		final AtomicBoolean trackerCalled = new AtomicBoolean(false);
		EventTracker eventTracker = new EventTracker() {

			@Override
			public void onEvent(Event event) {
				trackerCalled.set(true);
			}
		};
		Transaction tx = new Transaction(mock(WarUpdateTask.class), new TransactionId(1L), true, eventTracker);
		agent.submit(tx);
		Assert.assertTrue(trackerCalled.get());
	}

	@Test
	public void testTransactionLog() throws IOException {
		Task task = mock(WarUpdateTask.class);
		TransactionId txId = new TransactionId(1L);
		EventTracker eventTracker = new EventTracker() {

			@Override
			public void onEvent(Event event) {
			}
		};
		Transaction tx = new Transaction(task, txId, true, eventTracker );
		agent.submit(tx);
		char[] buf = new char[4096];
		agent.getLog(txId).read(buf);
		Assert.assertTrue(buf.length > 0);
	}

	@Test
	public void testTrackLifecycleEvent() {
		Task task = mock(Task.class);
		TransactionId txId = new TransactionId(1L);
		Transaction tx = new Transaction(task, txId, true, null);
		TaskProcessor taskProcessor = mock(TaskProcessor.class);
		final List<Status> statusHolder = new ArrayList<Task.Status>();
		doAnswer(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Transaction tx = (Transaction) invocation.getArguments()[0];
				tx.getEventTracker().onEvent(new LifecycleEvent("", statusHolder.get(0)));
				return null;
			}
		}).when(taskProcessor).submit(any(Transaction.class));
		TaskProcessorFactory.getInstance().registerTaskProcessor(task.getClass(), taskProcessor);

		for (final Status status : Status.values()) {
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

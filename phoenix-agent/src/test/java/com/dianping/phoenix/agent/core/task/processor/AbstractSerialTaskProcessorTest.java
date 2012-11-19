package com.dianping.phoenix.agent.core.task.processor;

import static org.mockito.Mockito.mock;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.TransactionId;
import com.dianping.phoenix.agent.core.event.AbstractEventTracker;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.task.Task.Status;
import com.dianping.phoenix.agent.core.task.processor.war.WarUpdateTask;

public class AbstractSerialTaskProcessorTest {

	private AbstractSerialTaskProcessor processor;

	@Before
	public void before() {
		processor = new AbstractSerialTaskProcessor() {

			@Override
			protected void doProcess(Transaction tx) {
			}
		};
	}

	@Test
	public void testLifecycleEvent() {
		final Status[] statusFlow = new Status[] { Status.PROCESSING, Status.SUCCESS, Status.COMMIT };
		final AtomicInteger nexStatus = new AtomicInteger(0);
		EventTracker eventTracker = new AbstractEventTracker() {

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				Assert.assertEquals(statusFlow[nexStatus.incrementAndGet()], event.getStatus());
			}
		};
		Transaction tx = new Transaction(mock(WarUpdateTask.class), new TransactionId(1L), false, eventTracker);
		processor.submit(tx);
		Assert.assertEquals(statusFlow.length, nexStatus.get());
	}

	@Test
	public void testSubmitWhileProcessing() {
		final AtomicBoolean txRejected = new AtomicBoolean(false);
		EventTracker eventTracker = new AbstractEventTracker() {

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				if (event.getStatus() == Status.REJECTED) {
					txRejected.set(true);
				}
			}

		};
		Transaction tx = new Transaction(mock(WarUpdateTask.class), new TransactionId(1L), false, eventTracker);
		processor.submit(tx);
		Transaction tx2 = new Transaction(mock(WarUpdateTask.class), new TransactionId(2L), false, eventTracker);
		processor.submit(tx2);
		Assert.assertTrue(txRejected.get());
	}

	@Test
	public void testSubmitAfterCommit() {
		Transaction tx = new Transaction(mock(WarUpdateTask.class), new TransactionId(1L), false,
				mock(EventTracker.class));
		processor.submit(tx);
		processor.commit(tx.getTxId());
		final AtomicBoolean txAccepted = new AtomicBoolean(true);
		Transaction tx2 = new Transaction(mock(WarUpdateTask.class), new TransactionId(2L), false,
				new AbstractEventTracker() {

					@Override
					protected void onLifecycleEvent(LifecycleEvent event) {
						if (event.getStatus() == Status.REJECTED) {
							txAccepted.set(false);
						}
					}

				});
		processor.submit(tx2);
		Assert.assertTrue(txAccepted.get());
	}

}

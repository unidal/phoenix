package com.dianping.phoenix.agent.core.task.processor;

import static org.mockito.Mockito.mock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.TransactionId;
import com.dianping.phoenix.agent.core.event.AbstractEventTracker;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.log.TransactionLog;
import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.Task.Status;
import com.dianping.phoenix.agent.core.task.processor.war.WarUpdateTask;

public class AbstractSerialTaskProcessorTest {

	private AbstractSerialTaskProcessor processor;

	@Before
	public void before() {
		processor = mock(AbstractSerialTaskProcessor.class);
	}

	@Test
	public void testCurrentTasks() {
		Task task = mock(Task.class);
		TaskProcessor mockProcessor = new AbstractSerialTaskProcessor() {

			@Override
			protected void doProcess(Transaction tx) {
				Assert.assertEquals(1, currentTransactions().size());
			}

			@Override
			public boolean cancel(TransactionId txId) {
				// TODO Auto-generated method stub
				return false;
			}

		};
		Assert.assertEquals(0, mockProcessor.currentTransactions().size());
		TaskProcessorFactory.getInstance().registerTaskProcessor(task.getClass(), mockProcessor);

		EventTracker eventTracker = mock(EventTracker.class);
		Transaction tx = new Transaction(task, new TransactionId(1L), eventTracker, mock(TransactionLog.class));
		mockProcessor.submit(tx);
		Assert.assertEquals(0, mockProcessor.currentTransactions().size());
	}

	@Test
	public void testSubmitWhileProcessing() {
		final AtomicBoolean txRejected = new AtomicBoolean(false);
		final CountDownLatch latch = new CountDownLatch(1);
		final EventTracker eventTracker = new AbstractEventTracker() {

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				if (event.getStatus() == Status.REJECTED) {
					txRejected.set(true);
					latch.countDown();
				}
			}

		};

		final TaskProcessor mockProcessor = new AbstractSerialTaskProcessor() {

			@Override
			protected void doProcess(Transaction tx) {
				try {
					latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			@Override
			public boolean cancel(TransactionId txId) {
				// TODO Auto-generated method stub
				return false;
			}

		};

		new Thread() {
			@Override
			public void run() {
				Transaction tx = new Transaction(mock(WarUpdateTask.class), new TransactionId(1L), eventTracker,
						mock(TransactionLog.class));
				mockProcessor.submit(tx);
			}

		}.start();

		Transaction tx2 = new Transaction(mock(WarUpdateTask.class), new TransactionId(2L), eventTracker,
				mock(TransactionLog.class));
		mockProcessor.submit(tx2);

		Assert.assertTrue(txRejected.get());
	}

	@Test
	public void testSubmitAfterCommit() {
		Transaction tx = new Transaction(mock(WarUpdateTask.class), new TransactionId(1L), mock(EventTracker.class),
				mock(TransactionLog.class));
		processor.submit(tx);
		final AtomicBoolean txAccepted = new AtomicBoolean(true);
		Transaction tx2 = new Transaction(mock(WarUpdateTask.class), new TransactionId(2L), new AbstractEventTracker() {

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				if (event.getStatus() == Status.REJECTED) {
					txAccepted.set(false);
				}
			}

		}, mock(TransactionLog.class));
		processor.submit(tx2);
		Assert.assertTrue(txAccepted.get());
	}

}

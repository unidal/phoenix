package com.dianping.phoenix.agent.core.task.processor;

import static org.mockito.Mockito.mock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.TransactionId;
import com.dianping.phoenix.agent.core.event.AbstractEventTracker;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.Task.Status;
import com.dianping.phoenix.agent.core.task.processor.war.WarUpdateTask;

public class AbstractSerialTaskProcessorTest extends ComponentTestCase {

	private AbstractSerialTaskProcessor<Task> processor;

	@Before
	public void before() {
		processor = mock(AbstractSerialTaskProcessor.class);
	}

	@Test
	public void testCurrentTasks() throws Exception {
		Task task = mock(Task.class);
		TaskProcessor<Task> mockProcessor = new AbstractSerialTaskProcessor<Task>() {

			@Override
			protected void doProcess(Transaction tx) {
				Assert.assertEquals(1, currentTransactions().size());
			}

			@Override
			public boolean cancel(TransactionId txId) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Class handle() {
				// TODO Auto-generated method stub
				return null;
			}

		};
		Assert.assertEquals(0, mockProcessor.currentTransactions().size());
		lookup(TaskProcessorFactory.class).registerTaskProcessor(task.getClass(), mockProcessor);

		EventTracker eventTracker = mock(EventTracker.class);
		Transaction tx = new Transaction(task, new TransactionId(1L), eventTracker);
		mockProcessor.submit(tx);
		Assert.assertEquals(0, mockProcessor.currentTransactions().size());
	}

	@Test
	public void testSubmitWhileProcessing() throws Exception {
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

		final TaskProcessor<Task> mockProcessor = new AbstractSerialTaskProcessor() {

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

			@Override
			public Class handle() {
				// TODO Auto-generated method stub
				return null;
			}

		};

		new Thread() {
			@Override
			public void run() {
				Transaction tx = new Transaction(mock(WarUpdateTask.class), new TransactionId(1L), eventTracker);
				try {
					mockProcessor.submit(tx);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}.start();

		Transaction tx2 = new Transaction(mock(WarUpdateTask.class), new TransactionId(2L), eventTracker);
		mockProcessor.submit(tx2);

		Assert.assertTrue(txRejected.get());
	}

	@Test
	public void testSubmitAfterCommit() {
		Transaction tx = new Transaction(mock(WarUpdateTask.class), new TransactionId(1L), mock(EventTracker.class));
		processor.submit(tx);
		final AtomicBoolean txAccepted = new AtomicBoolean(true);
		Transaction tx2 = new Transaction(mock(WarUpdateTask.class), new TransactionId(2L), new AbstractEventTracker() {

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

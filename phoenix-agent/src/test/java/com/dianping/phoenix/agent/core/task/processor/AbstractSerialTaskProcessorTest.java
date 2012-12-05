package com.dianping.phoenix.agent.core.task.processor;

import static org.mockito.Mockito.mock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.agent.core.event.AbstractEventTracker;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.tx.Transaction;
import com.dianping.phoenix.agent.core.tx.Transaction.Status;
import com.dianping.phoenix.agent.core.tx.TransactionId;

public class AbstractSerialTaskProcessorTest extends ComponentTestCase {

	@Before
	public void before() {
	}

	@Test
	public void testSubmitWhileProcessingSingleTaskProcessor() throws Exception {
		TaskProcessor<Task> a1 = lookup(MockTaskProcessorA1.class);
		TransactionId txId = new TransactionId(1L);
		final CountDownLatch latch = new CountDownLatch(1);
		Transaction tx1 = new Transaction(mock(Task.class), txId, new AbstractEventTracker() {

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				if (event.getStatus().isCompleted()) {
					latch.countDown();
				}
			}
		});

		Transaction tx2 = mock(Transaction.class);
		// accept
		SubmitResult submitResult1 = a1.submit(tx1);
		// reject
		SubmitResult submitResult2 = a1.submit(tx2);
		Assert.assertTrue(submitResult1.isAccepted());
		Assert.assertFalse(submitResult2.isAccepted());

		// make tx1 end and release Semaphore
		a1.cancel(txId);
		// Semaphore should has been released
		boolean awaitOk = latch.await(2, TimeUnit.SECONDS);
		if (awaitOk) {
			submitResult2 = a1.submit(tx2);
			Assert.assertTrue(submitResult2.isAccepted());
		} else {
			Assert.fail();
		}
	}

	@Test
	public void testSubmitWhileProcessingMultipleTaskProcessor() throws Exception {
		TaskProcessor<Task> a1 = lookup(MockTaskProcessorA1.class);
		TaskProcessor<Task> a2 = lookup(MockTaskProcessorA2.class);
		TransactionId txId = new TransactionId(1L);
		final CountDownLatch latch = new CountDownLatch(1);
		Transaction tx1 = new Transaction(mock(Task.class), txId, new AbstractEventTracker() {

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				if (event.getStatus().isCompleted()) {
					latch.countDown();
				}
			}
		});

		Transaction tx2 = mock(Transaction.class);
		// accept
		SubmitResult submitResult1 = a1.submit(tx1);
		// reject
		SubmitResult submitResult2 = a2.submit(tx2);
		Assert.assertTrue(submitResult1.isAccepted());
		Assert.assertFalse(submitResult2.isAccepted());

		// make tx1 end and release Semaphore
		a1.cancel(txId);
		// Semaphore should has been released
		boolean awaitOk = latch.await(2, TimeUnit.SECONDS);
		if (awaitOk) {
			submitResult2 = a2.submit(tx2);
			Assert.assertTrue(submitResult2.isAccepted());
		} else {
			Assert.fail();
		}

	}

	@Test
	public void testSubmitWhileUnrelatedTaskProcessorProcessing() throws Exception {
		TaskProcessor<Task> a1 = lookup(MockTaskProcessorA1.class);
		TaskProcessor<Task> b = lookup(MockTaskProcessorB.class);
		Transaction tx1 = mock(Transaction.class);
		Transaction tx2 = mock(Transaction.class);
		// accept
		SubmitResult submitResult1 = a1.submit(tx1);
		// accept
		SubmitResult submitResult2 = b.submit(tx2);
		Assert.assertTrue(submitResult1.isAccepted());
		Assert.assertTrue(submitResult2.isAccepted());
	}

	@Test
	public void testCurrentTransactions() throws Exception {
		TaskProcessor<Task> a1 = lookup(MockTaskProcessorA1.class);
		Assert.assertEquals(0, a1.currentTransactions().size());

		TransactionId txId = new TransactionId(1L);
		final CountDownLatch endLatch = new CountDownLatch(1);
		final CountDownLatch startLatch = new CountDownLatch(1);
		Transaction tx1 = new Transaction(mock(Task.class), txId, new AbstractEventTracker() {

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				if (event.getStatus().isCompleted()) {
					endLatch.countDown();
				} else if (event.getStatus() == Status.PROCESSING) {
					startLatch.countDown();
				}
			}
		});

		a1.submit(tx1);

		// when PROCESSING event published, current transaction should have been
		// recorded
		boolean awaitOk = startLatch.await(2, TimeUnit.SECONDS);
		if (!awaitOk) {
			Assert.fail();
		} else {
			Assert.assertEquals(1, a1.currentTransactions().size());
			Assert.assertEquals(tx1, a1.currentTransactions().get(0));
		}

		a1.cancel(txId);
		awaitOk = endLatch.await(2, TimeUnit.SECONDS);
		if (!awaitOk) {
			Assert.fail();
		} else {
			Assert.assertEquals(0, a1.currentTransactions().size());
		}

	}

	@Test
	public void testAttachEventTracker() throws Exception {
		TaskProcessor<Task> a1 = lookup(MockTaskProcessorA1.class);
		TransactionId txId = new TransactionId(1L);
		final CountDownLatch latch = new CountDownLatch(1);
		Transaction tx1 = new Transaction(mock(Task.class), txId, new AbstractEventTracker() {

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				if (event.getStatus().isCompleted()) {
					latch.countDown();
				}
			}
		});
		a1.submit(tx1);

		final AtomicBoolean eventReceived = new AtomicBoolean(false);
		a1.attachEventTracker(txId, new AbstractEventTracker() {

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				if (event.getStatus().isCompleted()) {
					eventReceived.set(true);
				}
			}
		});

		a1.cancel(txId);

		boolean awaitOk = latch.await(2, TimeUnit.SECONDS);
		if (!awaitOk) {
			Assert.fail();
		} else {
			Assert.assertTrue(eventReceived.get());
		}

	}

}

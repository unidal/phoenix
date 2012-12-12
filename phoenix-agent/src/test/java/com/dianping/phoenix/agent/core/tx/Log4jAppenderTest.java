package com.dianping.phoenix.agent.core.tx;

import java.io.ByteArrayOutputStream;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Test;

public class Log4jAppenderTest {

	@After
	public void after() {
		Log4jAppender.endTeeLog();
	}

	@Test
	public void testSingleThread() {
		Logger logger = Logger.getLogger(this.getClass());
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		UUID uuid = UUID.randomUUID();
		logger.info(uuid);
		Assert.assertFalse(out.toString().trim().endsWith(uuid.toString()));

		Log4jAppender.startTeeLog(out);
		uuid = UUID.randomUUID();
		logger.info(uuid);
		Assert.assertTrue(out.toString().trim().endsWith(uuid.toString()));

	}

	@Test
	public void testMultiThread() throws Exception {
		final Logger logger = Logger.getLogger(this.getClass());
		final ByteArrayOutputStream out1 = new ByteArrayOutputStream();
		final ByteArrayOutputStream out2 = new ByteArrayOutputStream();

		final String uuid11 = UUID.randomUUID().toString();
		final String uuid12 = UUID.randomUUID().toString();
		final String uuid21 = UUID.randomUUID().toString();
		final String uuid22 = UUID.randomUUID().toString();
		
		final CountDownLatch latch1 = new CountDownLatch(1);
		final CountDownLatch latch2 = new CountDownLatch(1);

		Thread t1 = new Thread() {
			public void run() {
				Log4jAppender.startTeeLog(out1);
				logger.info(uuid11);
				latch1.countDown();
				try {
					latch2.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				logger.info(uuid12);
			}
		};
		t1.start();

		Thread t2 = new Thread() {
			public void run() {
				try {
					latch1.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Log4jAppender.startTeeLog(out2);
				logger.info(uuid21);
				latch2.countDown();
				logger.info(uuid22);
			}
		};
		t2.start();

		t1.join();
		t2.join();
		
		Assert.assertTrue(out1.toString().indexOf(uuid11) > 0);
		Assert.assertTrue(out1.toString().indexOf(uuid12) > 0);
		Assert.assertTrue(out1.toString().indexOf(uuid21) < 0);
		Assert.assertTrue(out1.toString().indexOf(uuid22) < 0);
		
		Assert.assertTrue(out2.toString().indexOf(uuid11) < 0);
		Assert.assertTrue(out2.toString().indexOf(uuid12) < 0);
		Assert.assertTrue(out2.toString().indexOf(uuid21) > 0);
		Assert.assertTrue(out2.toString().indexOf(uuid22) > 0);
	}

}

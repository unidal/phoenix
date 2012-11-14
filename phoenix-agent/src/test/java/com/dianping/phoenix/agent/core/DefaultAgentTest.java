package com.dianping.phoenix.agent.core;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DefaultAgentTest {
	
	private Agent agent;
	
	@Before
	public void before() {
		agent = new DefaultAgent();
	}

	@Test
	public void testStartTrascactionTwice() {
		agent.startTransaction(1, "", mock(EventTracker.class));
		try {
			agent.startTransaction(1, "", mock(EventTracker.class));
			Assert.fail();
		}catch (IllegalStateException e) {
		}
	}
	
	@Test
	public void testStartTransactionWhileProcessing() {
		agent.startTransaction(1, "", mock(EventTracker.class));
		agent.process(mock(WarUpdateTask.class));
		try {
			agent.startTransaction(1, "", mock(EventTracker.class));
			Assert.fail();
		}catch (IllegalStateException e) {
		}
	}
	
	@Test
	public void testEventTracker() {
		final AtomicBoolean trackerCalled = new AtomicBoolean(false);
		agent.startTransaction(1L, "", new EventTracker() {
			
			@Override
			public void onEvent(Event event) {
				trackerCalled.set(true);
			}
		});
		agent.process(mock(WarUpdateTask.class));
		Assert.assertTrue(trackerCalled.get());
	}
	
	@Test
	public void testTransactionLog() throws IOException {
		agent.startTransaction(1L, "", new EventTracker() {
			
			@Override
			public void onEvent(Event event) {
			}
		});
		agent.process(mock(WarUpdateTask.class));
		byte[] buf = new byte[4096];
		agent.getLog(1L).read(buf);
		Assert.assertTrue(buf.length > 0);
	}
	
}

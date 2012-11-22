package com.dianping.phoenix.agent.core.task.processor.war;

import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.Test;

import com.dianping.phoenix.agent.core.TestUtil;
import com.dianping.phoenix.agent.core.Transaction;
import com.dianping.phoenix.agent.core.TransactionId;
import com.dianping.phoenix.agent.core.event.AbstractEventTracker;
import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.event.LifecycleEvent;
import com.dianping.phoenix.agent.core.log.FilePerTxLog;
import com.dianping.phoenix.agent.core.log.TransactionLog;

public class WarUpdateTaskProcessTest {

	@Test
	public void testLog() throws IOException {
		WarUpdateTaskProcessor processor = new WarUpdateTaskProcessor();
		TransactionLog txLog = new FilePerTxLog();
		TransactionId txId = TestUtil.generateTxId(1L);
		EventTracker eventTracker = new AbstractEventTracker() {

			@Override
			protected void onLifecycleEvent(LifecycleEvent event) {
				System.out.println(event.getStatus());
			}
			
		};
		Transaction tx = new Transaction(mock(WarUpdateTask.class), txId, eventTracker);
		processor.submit(tx);
		char[] cbuf = new char[4096];
		txLog.getLog(txId, 0).read(cbuf);
		System.out.println(new String(cbuf));
		System.in.read();
	}

}

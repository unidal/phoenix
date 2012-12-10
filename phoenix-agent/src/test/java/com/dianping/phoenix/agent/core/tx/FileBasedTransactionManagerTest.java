package com.dianping.phoenix.agent.core.tx;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import junit.framework.Assert;

import org.codehaus.plexus.util.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dianping.phoenix.agent.core.event.EventTracker;
import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.task.processor.kernel.DeployTask;

public class FileBasedTransactionManagerTest {

	TransactionManager txMgr;
	File rootDir;

	@Before
	public void before() {
		rootDir = new File(System.getProperty("java.io.tmpdir") + "/phoenix");
		rootDir.mkdirs();
		System.out.println(rootDir.getAbsolutePath());
		txMgr = new FileBasedTransactionManager(rootDir);
	}

	@After
	public void after() throws IOException {
		FileUtils.deleteDirectory(rootDir);
	}

	@Test
	public void testStartTransaction() throws IOException {
		TransactionId txId = new TransactionId(1L);
		Assert.assertTrue(txMgr.startTransaction(txId));
		Assert.assertFalse(txMgr.startTransaction(txId));
		txMgr.endTransaction(txId);
	}

	@Test
	public void testWriteReadLog() throws IOException {
		TransactionId txId = new TransactionId(1L);
		String msg = "你好";
		Writer writer = txMgr.getLogWriter(txId);
		writer.write(msg);
		writer.close();
		char[] cbuf = new char[100];
		int len = txMgr.getLogReader(txId, 0).read(cbuf);
		Assert.assertEquals(msg, new String(cbuf, 0, len));
	}

	@Test
	public void testSaveLoadTransaction() throws IOException {
		TransactionId txId = new TransactionId(1L);
		String domain = "user-web";
		String newVersion = "1.1";
		Task task = new DeployTask(domain, newVersion, "");
		Transaction tx = new Transaction(task, txId, EventTracker.DUMMY_TRACKER);
		txMgr.saveTransaction(tx);
		Transaction loadedTx = txMgr.loadTransaction(txId);
		
		Assert.assertEquals(tx.getStatus(), loadedTx.getStatus());
		Assert.assertEquals(tx.getTxId(), loadedTx.getTxId());
		Assert.assertEquals(tx.getTask().getClass(), loadedTx.getTask().getClass());
		Assert.assertEquals(tx.getTask(), loadedTx.getTask());
		
	}
	
}

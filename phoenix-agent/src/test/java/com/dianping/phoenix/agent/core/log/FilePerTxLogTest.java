package com.dianping.phoenix.agent.core.log;

import java.io.IOException;
import java.io.Writer;

import junit.framework.Assert;

import org.junit.Test;

import com.dianping.phoenix.agent.core.TransactionId;

public class FilePerTxLogTest {

	@Test
	public void test1() throws IOException {
		TransactionLog log = new FilePerTxLog();
		TransactionId txId = new TransactionId(1234L);
		String msg = "msg";
		Writer writer = log.getWriter(txId);
		writer.write(msg);
		writer.close();
		char[] buf = new char[10];
		int len = log.getLog(txId, 0).read(buf);
		Assert.assertEquals(msg, new String(buf, 0, len));
	}
	
}

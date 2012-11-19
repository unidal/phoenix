package com.dianping.phoenix.agent.core.log;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import com.dianping.phoenix.agent.core.TransactionId;

public class PropertyFileBasedLogTest {

	@Test
	public void test1() throws IOException {
		TransactionLog log = new PropertyFileBasedLog();
		TransactionId txId = new TransactionId(1234L);
		String msg = "msg";
		log.log(txId , msg);
		char[] buf = new char[10];
		int len = log.getLog(txId).read(buf);
		Assert.assertEquals(msg, new String(buf, 0, len));
	}
	
	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 4000; i++) {
			sb.append("a");
		}
		String msg = sb.toString();
		long start = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			TransactionLog log = new PropertyFileBasedLog();
			TransactionId txId = new TransactionId(i);
			log.log(txId , msg);
		}
		System.out.println(System.currentTimeMillis() - start);
	}
	
}

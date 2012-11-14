package com.dianping.phoenix.agent.core;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.util.StringInputStream;

public class InMemoryTransactionLog implements TransactionLog {

	private Map<Long, StringBuffer> cache = new HashMap<Long, StringBuffer>();
	
	@Override
	public void log(long txId, String log) {
		StringBuffer buffer = cache.get(txId);
		if(buffer == null) {
			buffer = new StringBuffer();
			cache.put(txId, buffer);
		}
		buffer.append(log);
		buffer.append("\n");
		System.out.println(buffer.toString());
	}

	@Override
	public void close(long txId) {
	}

	@Override
	public InputStream getLog(long txId) {
		return new StringInputStream(cache.get(txId).toString());
	}

}

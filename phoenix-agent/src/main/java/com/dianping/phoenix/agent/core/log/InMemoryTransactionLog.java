package com.dianping.phoenix.agent.core.log;

import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import com.dianping.phoenix.agent.core.TransactionId;

public class InMemoryTransactionLog implements TransactionLog {

	private Map<TransactionId, StringBuffer> cache = new HashMap<TransactionId, StringBuffer>();
	
	@Override
	public void log(TransactionId txId, String log) {
		StringBuffer buffer = cache.get(txId);
		if(buffer == null) {
			buffer = new StringBuffer();
			cache.put(txId, buffer);
		}
		buffer.append(log);
		buffer.append("\n");
	}

	@Override
	public void close(TransactionId txId) {
	}

	@Override
	public Reader getLog(TransactionId txId) {
		return new StringReader(cache.get(txId).toString());
	}

}

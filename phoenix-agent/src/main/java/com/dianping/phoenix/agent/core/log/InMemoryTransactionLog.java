package com.dianping.phoenix.agent.core.log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.phoenix.agent.core.TransactionId;

public class InMemoryTransactionLog implements TransactionLog {

	private final static String ENCODING = "UTF-8";
	private Map<TransactionId, ByteArrayOutputStream> cache = new ConcurrentHashMap<TransactionId, ByteArrayOutputStream>();

	@Override
	public OutputStream getOutputStream(TransactionId txId) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		cache.put(txId, bout);
		return bout;
	}

	@Override
	public Reader getLog(TransactionId txId, int offset) {
		return new InputStreamReader(new ByteArrayInputStream(cache.get(txId).toByteArray()));
	}

	@Override
	public Writer getWriter(TransactionId txId) throws IOException {
		return new OutputStreamWriter(getOutputStream(txId), ENCODING);
	}

}

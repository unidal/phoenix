package com.dianping.phoenix.agent.core.tx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class MockTransactionManager implements TransactionManager {
	
	Map<TransactionId, Transaction> txCache = new HashMap<TransactionId, Transaction>();
	Map<TransactionId, ByteArrayOutputStream> streamCache = new HashMap<TransactionId, ByteArrayOutputStream>();

	@Override
	public OutputStream getLogOutputStream(TransactionId txId) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		streamCache.put(txId, bout);
		return bout;
	}

	@Override
	public Reader getLogReader(TransactionId txId, int offset) throws IOException {
		ByteArrayOutputStream bout = streamCache.get(txId);
		if(bout != null) {
			byte[] fullBuf = bout.toByteArray();
			if(fullBuf.length > offset) {
			byte[] buf = new byte[fullBuf.length - offset];
			System.arraycopy(fullBuf, offset, buf, 0, buf.length);
			return new InputStreamReader(new ByteArrayInputStream(buf));
			} else {
				return null;
			}
		}
		return null;
	}

	@Override
	public Transaction loadTransaction(TransactionId txId) {
		return txCache.get(txId);
	}

	@Override
	public void saveTransaction(Transaction tx) throws IOException {
		txCache.put(tx.getTxId(), tx);
	}

	@Override
	public boolean transactionExists(TransactionId txId) {
		return txCache.containsKey(txId);
	}

	@Override
	public boolean startTransaction(TransactionId txId) {
		if(transactionExists(txId)) {
			return false;
		} else {
			txCache.put(txId, null);
			return true;
		}
	}

	@Override
	public void endTransaction(TransactionId txId) {
	}

	@Override
	public Writer getLogWriter(TransactionId txId) throws IOException {
		OutputStream out = getLogOutputStream(txId);
		if(out != null) {
			return new OutputStreamWriter(out);
		} else {
			return null;
		}
	}

}

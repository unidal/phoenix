package com.dianping.phoenix.agent.core.tx;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;


public interface TransactionManager {

	OutputStream getLogOutputStream(TransactionId txId) throws IOException;
	Writer getLogWriter(TransactionId txId) throws IOException;
	Reader getLogReader(TransactionId txId, int offset) throws IOException;
	Transaction loadTransaction(TransactionId txId);
	void saveTransaction(Transaction tx) throws IOException;
	boolean transactionExists(TransactionId txId);
	
	/**
	 * try to start a new transaction with <code>txId</code>
	 * @param txId
	 * @return whether <code>txId</code> is valid for a new transaction
	 */
	boolean startTransaction(TransactionId txId);
	
	/**
	 * do some clean up when an transaction ends
	 * @param txId
	 */
	void endTransaction(TransactionId txId);
	
}

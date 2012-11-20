package com.dianping.phoenix.agent.core.log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import com.dianping.phoenix.agent.core.TransactionId;

public interface TransactionLog {

	OutputStream getOutputStream(TransactionId txId) throws IOException;
	Writer getWriter(TransactionId txId) throws IOException;
	Reader getLog(TransactionId txId, int offset) throws IOException;
	
}

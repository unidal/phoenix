package com.dianping.phoenix.agent.core.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import org.apache.log4j.Logger;

import com.dianping.phoenix.agent.core.TransactionId;

public class FilePerTxLog implements TransactionLog {

	private final static Logger logger = Logger.getLogger(FilePerTxLog.class);

	private final static String LOG_ROOT = "/data/applogs/phoenix/transaction/";
	private final static String ENCODING = "UTF-8";

	private File txId2File(TransactionId txId) throws IOException {
		String normalizedTxId = txId.toReadableFormat();
		if (normalizedTxId.length() < 2) {
			normalizedTxId = "00" + normalizedTxId;
		}
		String prefixDir = normalizedTxId.substring(0, 2) + "/";
		String fileName = normalizedTxId.substring(2) + ".log";
		File file = new File(LOG_ROOT + prefixDir + fileName);
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	@Override
	public OutputStream getOutputStream(TransactionId txId) throws IOException {
		return new FileOutputStream(txId2File(txId), true);
	}

	@Override
	public Reader getLog(TransactionId txId, int offset) {
		try {
			Reader reader = new InputStreamReader(new FileInputStream(txId2File(txId)), ENCODING);
			reader.skip(offset);
			return reader;
		} catch (Exception e) {
			logger.error(String.format("can not open file %s", txId), e);
			return null;
		}
	}

	@Override
	public Writer getWriter(TransactionId txId) throws IOException {
		return new OutputStreamWriter(getOutputStream(txId), ENCODING);
	}

}

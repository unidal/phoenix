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

public class PropertyFileBasedLog implements TransactionLog {

	private final static Logger logger = Logger.getLogger(PropertyFileBasedLog.class);
	
	private final static String LOG_ROOT = "/Volumes/HDD2/tmp/log/";
	private final static String ENCODING = "UTF-8";
	
	private File txId2File(TransactionId txId) throws IOException {
		String normalizedTxId = txId.toReadableFormat();
		if(normalizedTxId.length() < 2) {
			normalizedTxId = "00" + normalizedTxId;
		}
		String prefixDir = normalizedTxId.substring(0, 2) + "/";
		String fileName = normalizedTxId.substring(2) + ".log";
		File file = new File(LOG_ROOT + prefixDir + fileName);
		if(!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		if(!file.exists()) {
			file.createNewFile();
		}
		return file;
	}
	
	@Override
	public void log(TransactionId txId, String log) {
		Writer writer = null;
		OutputStream fout;
		try {
			fout = new FileOutputStream(txId2File(txId), true);
			writer = new OutputStreamWriter(fout, ENCODING);
			writer.write(log);
			writer.close();
			fout.close();
		} catch (Exception e) {
			logger.error(String.format("can not write to file %s", txId), e);
		}
	}

	@Override
	public void close(TransactionId txId) {
		// TODO Auto-generated method stub

	}

	@Override
	public Reader getLog(TransactionId txId) {
		try {
			return new InputStreamReader(new FileInputStream(txId2File(txId)), ENCODING);
		} catch (Exception e) {
			logger.error(String.format("can not open file %s", txId), e);
			return null;
		}
	}

}

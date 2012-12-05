package com.dianping.phoenix.agent.core.tx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.plexus.util.IOUtil;

import com.dianping.phoenix.agent.core.task.Task;

public class FileBasedTransactionManager implements TransactionManager {

	private final static Logger logger = Logger.getLogger(FileBasedTransactionManager.class);

	private final static String DEFAULT_LOG_ROOT = "/data/applogs/phoenix/transaction/";

	private final static String ENCODING = "UTF-8";
	private static final String PROPERTIES_FILE = "tx.properties";
	private static final String LOG_FILE = "tx.log";
	private static final String LOCK_FILE = "tx.lock";

	private static final String KEY_TX_JSON = "txJson";

	private static final String KEY_TASK_JSON = "taskJson";

	private static final String KEY_TASK_CLASS = "taskClass";


	private ObjectMapper jsonMapper;
	private File logRootDir;

	public FileBasedTransactionManager() {
		this(new File(DEFAULT_LOG_ROOT));
	}

	public FileBasedTransactionManager(File logRootDir) {
		jsonMapper = new ObjectMapper();
		if (!logRootDir.exists()) {
			logRootDir.mkdirs();
		}
		this.logRootDir = logRootDir;
	}

	private File txId2BaseDir(TransactionId txId) {
		String normalizedTxId = txId.toReadableFormat();
		File txRootDir = new File(logRootDir, normalizedTxId);
		if (!txRootDir.exists()) {
			txRootDir.mkdirs();
		}
		return txRootDir;
	}

	private File txId2LogFile(TransactionId txId) {
		File logFile = new File(txId2BaseDir(txId), LOG_FILE);
		return logFile;
	}

	@Override
	public OutputStream getLogOutputStream(TransactionId txId) throws IOException {
		File logFile = txId2LogFile(txId);
		return new FileOutputStream(logFile, true);
	}

	@Override
	public Reader getLogReader(TransactionId txId, int offset) {
		Reader reader = null;
		File logFile = txId2LogFile(txId);
		if (logFile.exists()) {
			try {
				reader = new InputStreamReader(new FileInputStream(logFile), ENCODING);
				reader.skip(offset);
			} catch (Exception e) {
				logger.error(String.format("can not open file %s", txId), e);
			}
		}
		return reader;
	}

	@Override
	public Transaction loadTransaction(TransactionId txId) {
		File txPropertiesFile = txId2PropertiesFile(txId);
		Transaction tx = null;
		if (txPropertiesFile.exists()) {
			Properties props = new Properties();
			FileInputStream fin = null;
			try {
				fin = new FileInputStream(txPropertiesFile);
				props.load(fin);
				String txJson = props.getProperty(KEY_TX_JSON, "{}");
				String taskJson = props.getProperty(KEY_TASK_JSON, "{}");
				String taskClassName = props.getProperty(KEY_TASK_CLASS);
				Class<?> taskClass = Class.forName(taskClassName);

				tx = jsonMapper.readValue(txJson, Transaction.class);
				Task task = (Task) jsonMapper.readValue(taskJson, taskClass);
				tx.setTask(task);
			} catch (Exception e) {
				logger.error("error deserialize transaction object from properties file");
				throw new RuntimeException(e);
			} finally {
				IOUtil.close(fin);
			}
		}
		return tx;
	}

	@Override
	public void saveTransaction(Transaction tx) throws IOException {
		FileOutputStream fout = new FileOutputStream(txId2PropertiesFile(tx.getTxId()));
		Properties props = new Properties();
		props.setProperty(KEY_TX_JSON, jsonMapper.writeValueAsString(tx));
		props.setProperty(KEY_TASK_JSON, jsonMapper.writeValueAsString(tx.getTask()));
		props.setProperty(KEY_TASK_CLASS, tx.getTask().getClass().getCanonicalName());
		try {
			props.store(fout, "");
		} finally {
			IOUtil.close(fout);
		}
	}

	@Override
	public boolean transactionExists(TransactionId txId) {
		return txId2PropertiesFile(txId).exists();
	}

	private File txId2PropertiesFile(TransactionId txId) {
		File txJsonFile = new File(txId2BaseDir(txId), PROPERTIES_FILE);
		return txJsonFile;
	}
	
	private File txId2LockFile(TransactionId txId) {
		return new File(txId2BaseDir(txId), LOCK_FILE);
	}

	@Override
	public boolean startTransaction(TransactionId txId) {
		File txLockFile = txId2LockFile(txId);
		try {
			return txLockFile.createNewFile();
		} catch (IOException e) {
			logger.error(String.format("error create lock file %s", txLockFile.getAbsolutePath()), e);
			return false;
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

package com.dianping.phoenix.agent.core.log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.phoenix.agent.core.TransactionId;

/**
 * Utility class for dev & test
 * @author marsqing
 *
 */
public class InMemoryTransactionLog implements TransactionLog {

	/**
	 * Unified byte array based Input/OutputStream
	 * Notice: won't work if exceed Out's buf size
	 * @author marsqing
	 *
	 */
	static class ByteArrayStream {

		static class Out extends ByteArrayOutputStream {

			public Out() {
				super(1024 * 1024);
			}

			public byte[] getBuf() {
				return buf;
			}

			public int getCount() {
				return count;
			}
		}

		static class In extends ByteArrayInputStream {

			private Out out;
			public In(Out out) {
				super(out.getBuf());
				this.out = out;
			}

			@Override
			public synchronized int read() {
				count = out.getCount();
				return super.read();
			}

			@Override
			public synchronized int read(byte[] b, int off, int len) {
				count = out.getCount();
				return super.read(b, off, len);
			}

		}

		Out out = new Out();
		In in = new In(out);

		public ByteArrayOutputStream getOutputStream() {
			return out;
		}

		public InputStream getInputStream() {
			return in;
		}
	}

	private final static String ENCODING = "UTF-8";
	private Map<TransactionId, ByteArrayStream> cache = new ConcurrentHashMap<TransactionId, ByteArrayStream>();

	@Override
	public synchronized OutputStream getOutputStream(TransactionId txId) throws IOException {
		if (cache.containsKey(txId)) {
			return cache.get(txId).getOutputStream();
		} else {
			ByteArrayStream bout = new ByteArrayStream();
			cache.put(txId, bout);
			return bout.getOutputStream();
		}
	}

	@Override
	public Reader getLog(TransactionId txId, int offset) {
		ByteArrayStream bs = cache.get(txId);
		if (bs != null && bs.getOutputStream().size() > offset) {
			InputStream in = bs.getInputStream();
			try {
				in.skip(offset);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return new InputStreamReader(in);
		} else {
			return null;
		}
	}

	@Override
	public Writer getWriter(TransactionId txId) throws IOException {
		return new OutputStreamWriter(getOutputStream(txId), ENCODING);
	}
	
	public static void main(String[] args) throws IOException {
		InMemoryTransactionLog log = new InMemoryTransactionLog();
		TransactionId txId = new TransactionId(1L);
		log.getOutputStream(txId).write("a".getBytes());
		char[] cbuf = new char[4096];
		Reader reader = log.getLog(txId, 0);
		int len = reader.read(cbuf);
		System.out.println(cbuf[0]);
		len = reader.read(cbuf);
		System.out.println(len);
		log.getOutputStream(txId).write("b".getBytes());
		len = reader.read(cbuf);
		System.out.println(cbuf[0]);
	}

}

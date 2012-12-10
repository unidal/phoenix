package com.dianping.phoenix.agent.core.tx;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Layout;
import org.apache.log4j.WriterAppender;

public class Log4jAppender extends WriterAppender {

	public Log4jAppender() {
		setWriter(new OutputStreamWriter(new ThreadLocalOutputStream()));
	}

	@Override
	public void setLayout(Layout layout) {
		super.setLayout(layout);
	}

	@Override
	public void setName(String name) {
		super.setName(name);
	}
	
	public static void startTeeLog(OutputStream out) {
		ThreadLocalOutputStream.tlOut.set(out);
	}

	public static void endTeeLog() {
		ThreadLocalOutputStream.tlOut.set(null);
	}
	
	static class ThreadLocalOutputStream extends OutputStream {

		public static ThreadLocal<OutputStream> tlOut = new ThreadLocal<OutputStream>();

		@Override
		public void write(int b) throws IOException {
			if (tlOut.get() != null) {
				tlOut.get().write(b);
			}
		}

	}
	
}

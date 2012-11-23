package com.dianping.phoenix.service;

import java.text.MessageFormat;
import java.util.Date;

public class DefaultStatusReporter implements StatusReporter {
	private MessageFormat m_format = new MessageFormat(
			"[{0,date,yyyy-MM-dd HH:mm:ss}] {1}");

	@Override
	public void updateState(String state) {
		System.out.println(state);
	}

	@Override
	public synchronized void log(String message) {
		String log = m_format.format(new Object[] { new Date(), message });

		System.out.println(log);
	}

	@Override
	public synchronized void log(String message, Throwable e) {
		String log = m_format.format(new Object[] { new Date(), message });
		System.out.println(log);
		e.printStackTrace();
	}

}

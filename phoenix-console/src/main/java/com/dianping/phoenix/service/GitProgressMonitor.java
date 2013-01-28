package com.dianping.phoenix.service;

import java.util.concurrent.TimeUnit;

import org.eclipse.jgit.lib.BatchingProgressMonitor;


public class GitProgressMonitor extends BatchingProgressMonitor {
	private LogService m_log;

	private String m_key;

	public GitProgressMonitor(LogService log, String key) {
		m_key = key;
		m_log = log;

		setDelayStart(2, TimeUnit.SECONDS);
	}

	@Override
	protected void onEndTask(String taskName, int workCurr) {
		log(taskName, workCurr, -1, -1, true);
	}

	@Override
	protected void onEndTask(String taskName, int cmp, int totalWork, int pcnt) {
		log(taskName, cmp, totalWork, pcnt, true);
	}

	@Override
	protected void onUpdate(String taskName, int workCurr) {
		log(taskName, workCurr, -1, -1, false);
	}

	@Override
	protected void onUpdate(String taskName, int cmp, int totalWork, int pcnt) {
		log(taskName, cmp, totalWork, pcnt, false);
	}

	private void log(String taskName, int cmp, int totalWork, int pcnt, boolean isEnd) {
		StringBuilder sb = new StringBuilder(256);

		sb.append(taskName);
		sb.append(": ");

		while (sb.length() < 25) {
			sb.append(' ');
		}

		if (pcnt < 10) {
			sb.append(' ');
		}

		if (pcnt < 100) {
			sb.append(' ');
		}

		sb.append(pcnt);
		sb.append("% (").append(cmp).append('/').append(totalWork).append(')');

		if (isEnd) {
			sb.append('\n');
		}

		m_log.log(m_key, sb.toString());
	}
}

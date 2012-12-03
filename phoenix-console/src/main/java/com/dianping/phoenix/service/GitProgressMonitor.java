package com.dianping.phoenix.service;

import org.eclipse.jgit.lib.BatchingProgressMonitor;

import com.site.lookup.annotation.Inject;

public class GitProgressMonitor extends BatchingProgressMonitor {
	@Inject
	private StatusReporter m_reporter;

	private void format(StringBuilder s, String taskName, int workCurr) {
		s.append(taskName);
		s.append(": ");
		while (s.length() < 25)
			s.append(' ');
		s.append(workCurr);
	}

	private void format(StringBuilder s, String taskName, int cmp,
			int totalWork, int pcnt) {
		s.append(taskName);
		s.append(": ");
		while (s.length() < 25)
			s.append(' ');

		String endStr = String.valueOf(totalWork);
		String curStr = String.valueOf(cmp);
		while (curStr.length() < endStr.length())
			curStr = " " + curStr;
		if (pcnt < 100)
			s.append(' ');
		if (pcnt < 10)
			s.append(' ');
		s.append(pcnt);
		s.append("% (");
		s.append(curStr);
		s.append("/");
		s.append(endStr);
		s.append(")");
	}

	@Override
	protected void onEndTask(String taskName, int workCurr) {
		StringBuilder s = new StringBuilder();
		format(s, taskName, workCurr);
		s.append("\n");
		send(s);
	}

	@Override
	protected void onEndTask(String taskName, int cmp, int totalWork, int pcnt) {
		StringBuilder s = new StringBuilder();
		format(s, taskName, cmp, totalWork, pcnt);
		s.append("\n");
		send(s);
	}

	@Override
	protected void onUpdate(String taskName, int workCurr) {
		StringBuilder s = new StringBuilder();
		format(s, taskName, workCurr);
		send(s);
	}

	@Override
	protected void onUpdate(String taskName, int cmp, int totalWork, int pcnt) {
		StringBuilder s = new StringBuilder();
		format(s, taskName, cmp, totalWork, pcnt);
		send(s);
	}

	private void send(StringBuilder s) {
		m_reporter.log(s.toString());
	}

}

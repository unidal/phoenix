package com.dianping.maven.plugin.tools.vcs;

import org.eclipse.jgit.lib.BatchingProgressMonitor;

class GitCodeRetrieveProcessMonitor extends BatchingProgressMonitor{
	
	private LogService logService;
	
	public GitCodeRetrieveProcessMonitor(LogService logService) {
		super();
		this.logService = logService;
	}

	@Override
	protected void onUpdate(String taskName, int workCurr) {
		onUpdate(taskName, workCurr, -1, -1);
	}

	@Override
	protected void onEndTask(String taskName, int workCurr) {
		onEndTask(taskName, workCurr, -1,-1);
	}

	@Override
	protected void onUpdate(String taskName, int workCurr, int workTotal,
			int percentDone) {
		logToOutputStream(taskName, workCurr, workTotal, percentDone);
	}

	@Override
	protected void onEndTask(String taskName, int workCurr, int workTotal,
			int percentDone) {
		logToOutputStream(taskName, workCurr, workTotal, percentDone);
	}
	
	private void logToOutputStream(String taskName, int workCurr, int workTotal, int pcnt){
		StringBuilder sb = new StringBuilder(256);

		sb.append(taskName);
		sb.append(": ");

		//add spaces
		while (sb.length() < 25) {
			sb.append(' ');
		}
		if (pcnt < 10) {
			sb.append(' ');
		}
		if (pcnt < 100) {
			sb.append(' ');
		}

		sb.append(pcnt).append("% (").append(workCurr).
		   append('/').append(workTotal).append(')');
		
		logService.log(sb.toString());
	}

}

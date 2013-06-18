package com.dianping.maven.plugin.tools.vcs;

import org.eclipse.jgit.lib.BatchingProgressMonitor;

class GitCodeRetrieveProcessMonitor extends BatchingProgressMonitor {

    private LogService logService;
    private String     tips;

    public GitCodeRetrieveProcessMonitor(LogService logService, String tips) {
        super();
        this.logService = logService;
        this.tips = tips;
    }

    @Override
    protected void onUpdate(String taskName, int workCurr) {
        onUpdate(taskName, workCurr, -1, -1);
    }

    @Override
    protected void onEndTask(String taskName, int workCurr) {
        onEndTask(taskName, workCurr, -1, -1);
    }

    @Override
    protected void onUpdate(String taskName, int workCurr, int workTotal, int percentDone) {
        logToOutputStream(taskName, workCurr, workTotal, percentDone);
    }

    @Override
    protected void onEndTask(String taskName, int workCurr, int workTotal, int percentDone) {
        logToOutputStream(taskName, workCurr, workTotal, percentDone);
    }

    private void logToOutputStream(String taskName, int workCurr, int workTotal, int pcnt) {
        logService.updateProgressBar(pcnt, tips);
    }

}

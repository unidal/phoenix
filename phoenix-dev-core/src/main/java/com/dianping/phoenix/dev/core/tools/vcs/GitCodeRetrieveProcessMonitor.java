package com.dianping.phoenix.dev.core.tools.vcs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.lib.BatchingProgressMonitor;

class GitCodeRetrieveProcessMonitor extends BatchingProgressMonitor {

    private LogService                       logService;
    private String                           tips;
    private static Map<String, ProgressMeta> taskWeightMapping = new HashMap<String, ProgressMeta>();

    private static class ProgressMeta {
        int start;
        int weight;

        public ProgressMeta(int start, int weight) {
            this.start = start;
            this.weight = weight;
        }

    }

    static {
        taskWeightMapping.put("remote: Compressing objects", new ProgressMeta(0, 2));
        taskWeightMapping.put("Receiving objects", new ProgressMeta(2, 5));
        taskWeightMapping.put("Resolving deltas", new ProgressMeta(7, 2));
        taskWeightMapping.put("Updating references", new ProgressMeta(9, 1));
    }

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
        if (taskWeightMapping.containsKey(taskName)) {
            ProgressMeta progressMeta = taskWeightMapping.get(taskName);
            double progress = 100.0d * (progressMeta.start + pcnt * progressMeta.weight / 100.0d) / 10.0d;
            logService.updateProgressBar(progress, tips);
        }
    }

}

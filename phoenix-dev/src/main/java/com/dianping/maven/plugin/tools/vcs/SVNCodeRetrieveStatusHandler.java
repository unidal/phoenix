package com.dianping.maven.plugin.tools.vcs;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;

public class SVNCodeRetrieveStatusHandler implements ISVNEventHandler {

    private LogService logService;
    private String     tips;
    private int        totalFileCount;
    private int        currentFileCount = 0;

    SVNCodeRetrieveStatusHandler(LogService logService, String tips, int totalFileCount) {
        this.logService = logService;
        this.tips = tips;
        this.totalFileCount = totalFileCount;
        this.currentFileCount = 0;
    }

    @Override
    public void checkCancelled() throws SVNCancelException {
    }

    @Override
    public void handleEvent(SVNEvent event, double percentage) throws SVNException {
        if (event.getAction() == SVNEventAction.UPDATE_ADD && event.getNodeKind() == SVNNodeKind.FILE) {
            currentFileCount++;
            logService.updateProgressBar(currentFileCount * 100 / (double) totalFileCount, tips);
        }
    }
}

package com.dianping.maven.plugin.tools.vcs;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

public class SVNCodeRetriever implements ICodeRetriever {
    private SVNClientManager      svnClientManager = SVNClientManager.newInstance();
    private SVNCodeRetrieveConfig svnConfig;

    @SuppressWarnings("deprecation")
    @Override
    public void retrieveCode() {
        LogService logService = new LogService(svnConfig.getLogOutput());

        try {
            DAVRepositoryFactory.setup();
            SVNURL url = SVNURL.parseURIEncoded(svnConfig.getRepoUrl());
            File exportDir = new File(svnConfig.getLocalPath());
            exportDir.mkdirs();
            long versionNum = svnConfig.getVersion();
            SVNRevision version = null;
            if (versionNum > 0) {
                version = SVNRevision.create(svnConfig.getVersion());
            }

            final AtomicInteger fileCount = new AtomicInteger(0);

            ISVNDirEntryHandler dirHandler = new ISVNDirEntryHandler() {

                @Override
                public void handleDirEntry(SVNDirEntry dirEntry) throws SVNException {
                    if (dirEntry.getKind() == SVNNodeKind.FILE) {
                        fileCount.incrementAndGet();
                    }
                }
            };
            SVNLogClient logClient = svnClientManager.getLogClient();
            logClient.doList(url, SVNRevision.HEAD, SVNRevision.HEAD, false, SVNDepth.INFINITY, SVNDirEntry.DIRENT_ALL,
                    dirHandler);
            ISVNEventHandler eventHandler = new SVNCodeRetrieveStatusHandler(logService, String.format(
                    "Checking out %s", svnConfig.getRepoUrl()), fileCount.get());
            SVNUpdateClient updateClient = svnClientManager.getUpdateClient();
            updateClient.setEventHandler(eventHandler);
            updateClient.setIgnoreExternals(false);
            // checkout the latest version if version is null
            logService.log("Repository checkout start");
            logService.log(String.format("checkout to %s", exportDir.getAbsolutePath()));
            logService.log(System.getProperty("line.separator"));
            updateClient.doCheckout(url, exportDir, version, version, true);
            logService.log("Repository checkout end");
            logService.log(System.getProperty("line.separator"));
        } catch (Exception e) {
            throw new RetrieveException(e);
        }
    }

    @Override
    public void setConfig(CodeRetrieveConfig config) {
        svnConfig = (SVNCodeRetrieveConfig) config;
        svnConfig.validate();
    }

    public static void main(String[] args) {
        SVNCodeRetriever svnCodeRetriever = new SVNCodeRetriever();
        svnCodeRetriever.setConfig(new SVNCodeRetrieveConfig(
                "http://192.168.8.45:81/svn/dianping/dianping/shop/trunk/shop-web/", "/Users/leoleung/test",
                System.out, -1L));
        svnCodeRetriever.retrieveCode();
    }
}

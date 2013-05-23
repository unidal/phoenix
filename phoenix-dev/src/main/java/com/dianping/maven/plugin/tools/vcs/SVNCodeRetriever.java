package com.dianping.maven.plugin.tools.vcs;

import java.io.File;

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

public class SVNCodeRetriever implements ICodeRetriever{
	private SVNClientManager svnClientManager = SVNClientManager.newInstance();
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

			ISVNEventHandler eventHandler = new SVNCodeRetrieveStatusHandler(logService);
			SVNUpdateClient updateClient = svnClientManager.getUpdateClient();
			updateClient.setEventHandler(eventHandler);
			updateClient.setIgnoreExternals(false);
			// checkout the latest version if version is null
			logService.log("Repository checkout start");
			logService.log(String.format("checkout to %s", exportDir.getAbsolutePath()));
			updateClient.doCheckout(url, exportDir, version, version, true);
			logService.log("Repository checkout end");
		} catch (Exception e) {
			throw new RetrieveException(e);
		}
	}


	@Override
   public void setConfig(CodeRetrieveConfig config) {
		svnConfig = (SVNCodeRetrieveConfig)config; 
		svnConfig.validate();
   }
}

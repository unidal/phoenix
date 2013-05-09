package com.dianping.phoenix.dev.tools;

import java.io.File;

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

class SVNCodeRetriever implements ICodeRetriever{
	
	private static SVNClientManager svnClientManager = SVNClientManager.newInstance();
	private static ICodeRetriever instance = new SVNCodeRetriever();
	
	private SVNCodeRetriever(){}
	
	public static ICodeRetriever getInstance(){
		return instance;
	}

	@Override
	public void retrieveCode(CodeRetrieveConfig config) {
		SVNCodeRetrieveConfig svnConfig = (SVNCodeRetrieveConfig)config;
		LogService logService = new LogService(svnConfig.getLogOutput());
		svnConfig.validate();
		
		try{
			DAVRepositoryFactory.setup();
			SVNURL url = SVNURL.parseURIEncoded(svnConfig.getRepoUrl());
			File exportDir = new File(svnConfig.getLocalPath());
			exportDir.mkdirs();
			long versionNum = svnConfig.getVersion();
			SVNRevision version = null;
			if(versionNum > 0){
				version = SVNRevision.create(svnConfig.getVersion());
			}
			
			ISVNEventHandler eventHandler = new SVNCodeRetrieveStatusHandler(logService);
			SVNUpdateClient updateClient = svnClientManager.getUpdateClient();
			updateClient.setEventHandler(eventHandler);
			updateClient.setIgnoreExternals(false);
			//checkout the latest version if version is null
			logService.log("Repository checkout start");
			updateClient.doCheckout(url, 
					exportDir, 
					version, 
					version, true);
			logService.log("Repository checkout end");
		}catch(Exception e){
			throw new RetrieveException(e);
		}
	}
	
	public static void main(String[] args){
		ICodeRetriever retriever = new SVNCodeRetriever();
		CodeRetrieveConfig config = new SVNCodeRetrieveConfig(
				"http://192.168.8.45:81/svn/dianping/platform/data-analysis/trunk/log-system", 
				"/tmp/test4", 
				"yix.zhang", 
				"7322281", 
				System.out,
				-1);
		retriever.retrieveCode(config);
	}

}

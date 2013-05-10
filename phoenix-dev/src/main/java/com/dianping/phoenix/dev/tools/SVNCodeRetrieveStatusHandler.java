package com.dianping.phoenix.dev.tools;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;

public class SVNCodeRetrieveStatusHandler implements ISVNEventHandler{
	
	private LogService logService;
	
	SVNCodeRetrieveStatusHandler(LogService logService){
		this.logService = logService;
	}

	@Override
	public void checkCancelled() throws SVNCancelException {}

	@Override
	public void handleEvent(SVNEvent event, double percentage) throws SVNException {
		SVNEventAction action = event.getAction();
		StringBuilder sb = new StringBuilder(256);
		sb.append(action.toString()).append(" ");
		while(sb.length() < 25){
			sb.append(" ");
		}
		if(action.equals(SVNEventAction.UPDATE_ADD)){
			sb.append(event.getFile().toString());
		}
		logService.log(sb.toString());
	}
}

package com.dianping.maven.plugin.configure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public enum Whiteboard {

	INSTANCE;
	
	private static Logger log = Logger.getLogger(Whiteboard.class);
	
	private List<WorkspaceInitializedListener> listeners = new ArrayList<WorkspaceInitializedListener>();
	
	public void addWorkspaceInitializedListener(WorkspaceInitializedListener listener) {
		listeners.add(listener);
	}
	
	public void workspaceInitialized(File wsDir) {
		for (WorkspaceInitializedListener listener : listeners) {
			try {
				listener.onWorkspaceInitialized(wsDir);
			} catch (RuntimeException e) {
				log.warn("error in listener", e);
			}
		}
	}
	
}

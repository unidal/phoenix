package com.dianping.maven.plugin.configure;

import java.io.File;

public interface WorkspaceInitializedListener {

	void onWorkspaceInitialized(File wsDir);
	
}

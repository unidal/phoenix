package com.dianping.phoenix.dev.core.configure;

import java.io.File;

public interface WorkspaceInitializedListener {

	void onWorkspaceInitialized(File wsDir);
	
}

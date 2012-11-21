package com.dianping.phoenix.agent.core.shell;

public interface ExecuteResultCallback {

	void onProcessCompleted(int exitCode);
	
	void onProcessFailed(Exception e);
	
}

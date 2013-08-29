package com.dianping.phoenix.dev.agent.shell;

public interface ExecuteResultCallback {

	void onProcessCompleted(int exitCode);
	
	void onProcessFailed(Exception e);
	
}

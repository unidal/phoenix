package com.dianping.phoenix.dev.agent.shell;

import java.io.IOException;
import java.io.OutputStream;

public interface ScriptExecutor {

	int exec(String scriptPath, OutputStream stdOut, OutputStream stdErr) throws IOException;
	void exec(String scriptPath, OutputStream stdOut, OutputStream stdErr, ExecuteResultCallback callback) throws IOException;
	void kill();
	
}
package com.dianping.phoenix.agent.core.shell;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

public class DefaultScriptExecutor implements ScriptExecutor {

	private ExecuteWatchdog watchdog;
	private DefaultExecutor executor;

	public DefaultScriptExecutor() {
		executor = new DefaultExecutor();
		watchdog = new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT);
		executor.setWatchdog(watchdog);
	}

	@Override
	public int exec(String scriptPath, OutputStream stdOut, OutputStream stdErr) throws IOException {
		PumpStreamHandler streamHandler = new PumpStreamHandler(stdOut, stdErr);
		CommandLine cmd = CommandLine.parse(scriptPath);
		executor.setExitValues(null);
		executor.setStreamHandler(streamHandler);
		int exitCode = executor.execute(cmd);
		return exitCode;
	}

	@Override
	public void kill() {
		watchdog.destroyProcess();
	}

}

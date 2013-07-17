package com.dianping.phoenix.dev.agent.shell;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;
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
	public void exec(String scriptPath, OutputStream stdOut, OutputStream stdErr,
			final ExecuteResultCallback execCallback) throws IOException {
		PumpStreamHandler streamHandler = new PumpStreamHandler(stdOut, stdErr);
		CommandLine cmd = CommandLine.parse(scriptPath);
		executor.setExitValues(null);
		executor.setStreamHandler(streamHandler);
		ExecuteResultHandler execResultHandler = new ExecuteResultHandler() {

			@Override
			public void onProcessFailed(ExecuteException e) {
				if (execCallback != null) {
					execCallback.onProcessFailed(e);
				}
			}

			@Override
			public void onProcessComplete(int exitCode) {
				if (execCallback != null) {
					execCallback.onProcessCompleted(exitCode);
				}
			}
		};
		executor.execute(cmd, execResultHandler);
	}

	@Override
	public void kill() {
		watchdog.destroyProcess();
	}

	@Override
	public int exec(String scriptPath, OutputStream stdOut, OutputStream stdErr) throws IOException {
		PumpStreamHandler streamHandler = new PumpStreamHandler(stdOut, stdErr);
		CommandLine cmd = CommandLine.parse(scriptPath);
		executor.setExitValues(null);
		executor.setStreamHandler(streamHandler);
		return executor.execute(cmd);
	}

}

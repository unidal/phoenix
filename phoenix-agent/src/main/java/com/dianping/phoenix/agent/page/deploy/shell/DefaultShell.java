package com.dianping.phoenix.agent.page.deploy.shell;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

public class DefaultShell implements Shell {
	@Override
	public void activate(OutputStream outputCollector) throws IOException {
		CommandLine cmd = CommandLine.parse(getScriptPath() + " activate");
		getExecutor(outputCollector).execute(cmd);
	}

	@Override
	public void commit(OutputStream outputCollector) throws IOException {
		CommandLine cmd = CommandLine.parse(getScriptPath() + " commit");
		getExecutor(outputCollector).execute(cmd);
	}

	private DefaultExecutor getExecutor(OutputStream outputCollector) {
		DefaultExecutor executor = new DefaultExecutor();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputCollector);

		executor.setExitValues(null);
		executor.setStreamHandler(streamHandler);
		return executor;
	}

	private String getScriptPath() {
		URL scriptUrl = this.getClass().getClassLoader().getResource("egret.sh");
		if(scriptUrl == null) {
			throw new RuntimeException("egret.sh not found");
		}
		return scriptUrl.getPath();
	}

	public static void main(String[] args) {
		(new DefaultShell()).getScriptPath();
	}

	@Override
	public void prepare(String libVersion, final OutputStream outputCollector) throws IOException {
		CommandLine cmd = CommandLine.parse(getScriptPath() + " prepare " + libVersion);
		getExecutor(outputCollector).execute(cmd);
	}

	@Override
	public void rollback(String appVersion, OutputStream outputCollector) throws IOException {
		CommandLine cmd = CommandLine.parse(getScriptPath() + " rollback " + appVersion);
		getExecutor(outputCollector).execute(cmd);
	}
}

package com.dianping.phoenix.agent.core.task.workflow;

import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.unidal.lookup.ContainerHolder;

import com.dianping.phoenix.agent.core.task.Task;
import com.dianping.phoenix.agent.core.tx.LogFormatter;

public class Context extends ContainerHolder {

	private LogFormatter logFormatter;
	private OutputStream logOut;
	private Step endStep;
	private Task task;
	private int exitCode;
	private AtomicBoolean killed = new AtomicBoolean(false);

	public Step getEndStep() {
		return endStep;
	}

	public int getExitCode() {
		return exitCode;
	}

	public LogFormatter getLogFormatter() {
		return logFormatter;
	}

	public OutputStream getLogOut() {
		return logOut;
	}

	public boolean isKilled() {
		return killed.get();
	}

	public void setEndStep(Step endStep) {
		this.endStep = endStep;
	}

	public void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}

	public void setKilled(boolean killed) {
		this.killed.set(killed);
	}

	public void setLogFormatter(LogFormatter logFormatter) {
		this.logFormatter = logFormatter;
	}

	public void setLogOut(OutputStream logOut) {
		this.logOut = logOut;
	}

	public boolean kill() {
		setKilled(true);
		return true;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}
}

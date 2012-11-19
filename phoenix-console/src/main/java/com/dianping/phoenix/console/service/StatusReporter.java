package com.dianping.phoenix.console.service;

public interface StatusReporter {
	public void updateState(String state);

	public void log(String message);

	public void log(String message, Throwable e);
}

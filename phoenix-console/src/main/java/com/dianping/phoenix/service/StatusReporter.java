package com.dianping.phoenix.service;

public interface StatusReporter {
	public void updateState(String state);

	public void log(String message);

	public void log(String message, Throwable e);
}

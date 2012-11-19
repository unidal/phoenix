package com.dianping.phoenix.service;


public class DefaultStatusReporter implements StatusReporter {
	@Override
	public void updateState(String state) {

	}

	@Override
	public void log(String message) {
		System.out.println(message);
	}

	@Override
	public void log(String message, Throwable e) {
		System.out.println(message);
		e.printStackTrace();
	}
}

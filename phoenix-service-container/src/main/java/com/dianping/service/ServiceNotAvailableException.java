package com.dianping.service;

public class ServiceNotAvailableException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ServiceNotAvailableException(String message) {
		super(message);
	}

	public ServiceNotAvailableException(String message, Throwable cause) {
		super(message, cause);
	}
}

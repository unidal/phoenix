package com.dianping.phoenix.dev.tools;

public class RetrieveException extends RuntimeException{

	private static final long serialVersionUID = 7119329923263258564L;

	public RetrieveException(String message, Throwable cause) {
		super(message, cause);
	}

	public RetrieveException(String message) {
		super(message);
	}

	public RetrieveException(Throwable cause) {
		super(cause);
	}

}

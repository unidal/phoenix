package com.dianping.phoenix.dev.core.tools.vcs;

public class ConfigIncompleteException extends RuntimeException{

	private static final long serialVersionUID = -8897733376357669737L;

	public ConfigIncompleteException(String msg, Throwable exception) {
		super(msg, exception);
	}

	public ConfigIncompleteException(String msg) {
		super(msg);
	}

	public ConfigIncompleteException(Throwable cause) {
		super(cause);
	}	
}

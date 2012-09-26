package com.dianping.kernel;

/**
 * @author bin.miao
 *
 */
public class TomcatRegisterException extends Exception{
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 2480957591886954508L;

	public TomcatRegisterException() {
			super();
	 } 
	 
	 public TomcatRegisterException(String message) {
			super(message);
	 }
	 
	 public TomcatRegisterException(Throwable cause) {
	        super(cause);
	 }
	 
	 public TomcatRegisterException(String message, Throwable cause) {
	        super(message, cause);
	 }

}

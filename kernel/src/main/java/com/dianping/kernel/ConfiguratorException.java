package com.dianping.kernel;

/**
 * @author bin.miao
 *
 */
public class ConfiguratorException extends RuntimeException{
	
	public ConfiguratorException(){
		super();
	}

	public ConfiguratorException(String message){
		super(message);
	}
	
	public ConfiguratorException(Throwable throwable){
		super(throwable);
	}
	
	public ConfiguratorException(String message,Throwable throwable){
		super(message,throwable);
	}
}

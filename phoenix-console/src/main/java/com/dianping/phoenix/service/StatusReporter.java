package com.dianping.phoenix.service;

import java.util.List;

public interface StatusReporter {
	public void updateState(String state);

	public void log(String message);

	public void log(String message, Throwable e);
	
	public void log(String category,String subCategory,String message);
	
	public void categoryLog(String category,String subCategory,String message, Throwable e);
	
	public List<String> getMessage(String category,String subCategory,int index);
	
	public void clearMessage(String category,String subCategory);
}

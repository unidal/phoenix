package com.dianping.phoenix.router;


public interface Rule {

	boolean match(String url);
	String trans(String url);
	
}

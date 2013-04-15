package com.dianping.phoenix.router.filter;


public interface Rule {

	boolean match(String url);
	String map(String url);
	
}

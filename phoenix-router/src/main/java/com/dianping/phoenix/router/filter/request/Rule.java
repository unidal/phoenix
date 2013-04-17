package com.dianping.phoenix.router.filter.request;


public interface Rule {

	boolean match(String url);
	String map(String url);
	
}

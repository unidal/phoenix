package com.dianping.phoenix.router.urlfilter;


public interface Rule {

	boolean match(String url);
	String map(String url);
	
}

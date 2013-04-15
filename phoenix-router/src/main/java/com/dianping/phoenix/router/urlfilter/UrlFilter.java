package com.dianping.phoenix.router.urlfilter;

import java.io.IOException;

public interface UrlFilter {

	UrlHolder filter(UrlHolder urlHolder, FilterChain filterChain) throws IOException;
	
}

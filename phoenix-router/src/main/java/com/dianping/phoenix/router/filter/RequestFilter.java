package com.dianping.phoenix.router.filter;

import java.io.IOException;

public interface RequestFilter {

	RequestHolder filter(RequestHolder urlHolder, FilterChain filterChain) throws IOException;
	
}

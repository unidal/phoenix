package com.dianping.phoenix.router.filter;

import java.io.IOException;

public interface FilterChain {

	RequestHolder doFilter(RequestHolder urlHolder) throws IOException;
	
}

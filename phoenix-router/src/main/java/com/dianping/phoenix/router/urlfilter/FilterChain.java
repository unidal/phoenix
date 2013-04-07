package com.dianping.phoenix.router.urlfilter;

import java.io.IOException;

public interface FilterChain {

	UrlHolder doFilter(UrlHolder urlHolder) throws IOException;
	
}

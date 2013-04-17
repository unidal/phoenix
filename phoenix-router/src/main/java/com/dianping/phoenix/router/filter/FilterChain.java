package com.dianping.phoenix.router.filter;

import java.io.IOException;

public interface FilterChain<C> {

	C doFilter(C ctx) throws IOException;
	
}

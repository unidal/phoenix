package com.dianping.phoenix.router.filter;

import java.io.IOException;


public interface Filter<C> {

	C filter(C ctx, FilterChain<C> filterChain) throws IOException;
	
}

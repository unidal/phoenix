package com.dianping.phoenix.router.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.ContainerHolder;


public class AbstractFilterChain<C> extends ContainerHolder implements FilterChain<C> {
	
	private List<Filter<C>> filterList = new ArrayList<Filter<C>>();
	private int curFilterIdx = 0;

	@Override
	public C doFilter(C ctx) throws IOException {
		if (curFilterIdx < filterList.size()) {
			return filterList.get(curFilterIdx++).filter(ctx, this);
		}
		return ctx;
	}
	
	protected void addFilter(Filter<C> filter) {
		filterList.add(filter);
	}

}

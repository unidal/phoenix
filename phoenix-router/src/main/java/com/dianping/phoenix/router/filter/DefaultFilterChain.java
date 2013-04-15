package com.dianping.phoenix.router.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

public class DefaultFilterChain extends ContainerHolder implements FilterChain, Initializable {

	@Inject
	private F5UrlFilter f5Filter;
	@Inject
	private UrlRewriteFilter urlRewriteFilter;
	@Inject
	private HeaderFilter headerFilter;

	private List<RequestFilter> filterList = new ArrayList<RequestFilter>();
	private int curFilterIdx = 0;

	public DefaultFilterChain() {
	}
	
	public DefaultFilterChain(RequestFilter ... urlFilters) {
		for (RequestFilter filter : urlFilters) {
			filterList.add(filter);
		}
	}

	@Override
	public RequestHolder doFilter(RequestHolder urlHolder) throws IOException {
		if (curFilterIdx < filterList.size()) {
			return filterList.get(curFilterIdx++).filter(urlHolder, this);
		}
		return urlHolder;
	}

	@Override
	public void initialize() throws InitializationException {
		filterList.add(f5Filter);
		filterList.add(urlRewriteFilter);
		filterList.add(headerFilter);
	}

}

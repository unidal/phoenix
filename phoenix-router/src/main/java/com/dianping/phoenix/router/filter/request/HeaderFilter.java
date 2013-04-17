package com.dianping.phoenix.router.filter.request;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dianping.phoenix.router.filter.Filter;
import com.dianping.phoenix.router.filter.FilterChain;

public class HeaderFilter implements Filter<RequestHolder> {
	
	private Set<String> headerToRemoveSet = new HashSet<String>();

	public HeaderFilter() {
		headerToRemoveSet = new HashSet<String>();
		headerToRemoveSet.add("Host");
		headerToRemoveSet.add("Content-Length");
		// headerToRemoveSet.add("Referer");
	}

	@Override
	public RequestHolder filter(RequestHolder urlHolder, FilterChain<RequestHolder> filterChain) throws IOException {
		Iterator<Map.Entry<String, List<String>>> iter = urlHolder.getHeaders().entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry<String, List<String>> entry = iter.next();
			if(headerToRemoveSet.contains(entry.getKey())) {
				iter.remove();
			}
		}
		return urlHolder;
	}

}

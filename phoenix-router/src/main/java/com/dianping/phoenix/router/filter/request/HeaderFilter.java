package com.dianping.phoenix.router.filter.request;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.dianping.phoenix.router.filter.Filter;
import com.dianping.phoenix.router.filter.FilterChain;

public class HeaderFilter implements Filter<RequestHolder> {

	private Set<String> headerToRemoveSet = new HashSet<String>();
	private final static Pattern pattern = Pattern.compile("([^:]+://)([^/]+)(.*)");

	public HeaderFilter() {
		headerToRemoveSet = new HashSet<String>();
		headerToRemoveSet.add("Host");
		headerToRemoveSet.add("Content-Length");
	}

	@Override
	public RequestHolder filter(RequestHolder urlHolder, FilterChain<RequestHolder> filterChain) throws IOException {
		Iterator<Map.Entry<String, List<String>>> iter = urlHolder.getHeaders().entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, List<String>> entry = iter.next();
			String key = entry.getKey();
			if (headerToRemoveSet.contains(key)) {
				iter.remove();
			}

			if ("Referer".equals(key) && entry.getValue().size() > 0) {
				reconstrcutRefererIfNeeded(urlHolder.getHost(), urlHolder.getPort(), entry.getValue());
			}

		}
		return urlHolder;
	}

	/**
	 * naming by leoleung
	 * @param reqHostPort
	 * @param reqPort
	 * @param headerValueList
	 */
	void reconstrcutRefererIfNeeded(String reqHostPort, int reqPort, List<String> headerValueList) {
		String value = headerValueList.get(0);
		if (StringUtils.isNotBlank(value)) {
			Matcher matcher = pattern.matcher(value);
			if (matcher.matches()) {
				String refererProtocolPart = matcher.group(1);
				String refererHostPort = matcher.group(2);
				String refererPathPart = matcher.group(3);
				if (reqPort > 0) {
					reqHostPort = reqHostPort + ":" + reqPort;
				}
				if (!StringUtils.equalsIgnoreCase(refererHostPort, reqHostPort)) {
					String newReferer = refererProtocolPart + reqHostPort + refererPathPart;
					headerValueList.set(0, newReferer);
				}
			} else {
				throw new RuntimeException("error parse Referer " + value);
			}
		}
	}

}

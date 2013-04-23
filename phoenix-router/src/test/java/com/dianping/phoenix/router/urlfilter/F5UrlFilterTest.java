package com.dianping.phoenix.router.urlfilter;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.router.filter.FilterChain;
import com.dianping.phoenix.router.filter.request.DefaultFilterChain;
import com.dianping.phoenix.router.filter.request.F5UrlFilter;
import com.dianping.phoenix.router.filter.request.RequestHolder;

public class F5UrlFilterTest extends ComponentTestCase {

	F5UrlFilter f5Filter;

	@Before
	public void before() throws Exception {
		f5Filter = lookup(F5UrlFilter.class);
	}

	private static String userWebPrefix = "http://127.0.0.1:9090/_user-web";
	private static String shopWebPrefix = "http://127.0.0.1:9090/_shop-web";

	@Test
	public void testPreserveUrl() throws IOException {
		match(userWebPrefix, "/member/1");
		match(userWebPrefix, "/photos/11/photocenter/editXX");
		notMatch(userWebPrefix, "/photos/A/photocenter/edit");

		match(shopWebPrefix, "/shop/123");
	}

	private void notMatch(String urlPrefix, String path) throws IOException {
		test(urlPrefix, path, false);
	}

	private void match(String urlPrefix, String path) throws IOException {
		test(urlPrefix, path, true);
	}

	private void test(String urlPrefix, String path, boolean match) throws IOException {
		String expectedUrl = urlPrefix + path;
		
		RequestHolder urlHolder = new RequestHolder();
		urlHolder.setPath(path);
		urlHolder.setHost("127.0.0.1");
		urlHolder.setPort(9090);
		urlHolder.setProtocol("http");
		
		@SuppressWarnings("unchecked")
		FilterChain<RequestHolder> fc = new DefaultFilterChain(f5Filter);
		String newUrl = fc.doFilter(urlHolder).toUrl();
		
		if (match) {
			Assert.assertEquals(expectedUrl, newUrl);
		} else {
			Assert.assertFalse(expectedUrl.equals(newUrl));
		}
	}

}

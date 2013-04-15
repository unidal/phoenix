package com.dianping.phoenix.router.urlfilter;

import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;

public class UrlTest {

	@Test
	public void testUrlPart() throws MalformedURLException {
		URL url = new URL("http://127.0.0.1:9090/a/b/c?key=value");
		Assert.assertEquals("http", url.getProtocol());
		Assert.assertEquals("127.0.0.1", url.getHost());
		Assert.assertEquals(9090, url.getPort());
		Assert.assertEquals("/a/b/c", url.getPath());
		Assert.assertEquals("key=value", url.getQuery());
	}
	
}

package com.dianping.phoenix.router.filter.request;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class HeaderFilterTest {

	@Test
	public void hostAndPortWillChange() {
		HeaderFilter f = new HeaderFilter();
		List<String> headerValueList = Arrays.asList(new String[]{"http://dev.local.dp/shop/123"});
		f.reconstrcutRefererIfNeeded("w.51ping.com", 8080, headerValueList);
		Assert.assertEquals("http://w.51ping.com:8080/shop/123", headerValueList.get(0));
	}
	
	@Test
	public void hostWillChange() {
		HeaderFilter f = new HeaderFilter();
		List<String> headerValueList = Arrays.asList(new String[]{"http://dev.local.dp:8080/shop/123"});
		f.reconstrcutRefererIfNeeded("w.51ping.com", 8080, headerValueList);
		Assert.assertEquals("http://w.51ping.com:8080/shop/123", headerValueList.get(0));
	}
	
	@Test
	public void portWillChange() {
		HeaderFilter f = new HeaderFilter();
		List<String> headerValueList = Arrays.asList(new String[]{"http://dev.local.dp/shop/123"});
		f.reconstrcutRefererIfNeeded("w.51ping.com", 0, headerValueList);
		Assert.assertEquals("http://w.51ping.com/shop/123", headerValueList.get(0));
	}
	
	@Test
	public void portWillChange2() {
		HeaderFilter f = new HeaderFilter();
		List<String> headerValueList = Arrays.asList(new String[]{"http://dev.local.dp:8080/shop/123"});
		f.reconstrcutRefererIfNeeded("w.51ping.com", 0, headerValueList);
		Assert.assertEquals("http://w.51ping.com/shop/123", headerValueList.get(0));
	}

}

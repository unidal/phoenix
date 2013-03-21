package com.dianping.phoenix.router;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class RouteServiceTest extends ComponentTestCase {

	RouteService rs;

	@Before
	public void before() throws Exception {
		rs = lookup(RouteService.class);
	}

	@Test
	public void testPreserveUrl() {
		Assert.assertTrue(match("_user-web", "/member/1"));
		Assert.assertTrue(match("_user-web", "/photos/11/photocenter/editXX"));
		
		Assert.assertFalse(match("_user-web", "/photos/A/photocenter/edit"));
	}

	private boolean match(String urlPrefix, String originUrl) {
		String expectedUrl = urlPrefix + originUrl;
		return expectedUrl.equals(rs.getTargetUrl(originUrl, ""));
	}

}

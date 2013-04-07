package com.dianping.phoenix.router;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.router.RequestMapper.REQUEST_TYPE;

public class RequestMapperTest extends ComponentTestCase {

	RequestMapper reqTrans;
	
	@Before
	public void before() throws Exception {
		reqTrans = lookup(RequestMapper.class);
	}

	@Test
	public void testShouldPreserveHeader() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		String[] headerNames = new String[]{"a", "b"};
		when(req.getHeaderNames()).thenReturn(makeEnumeration(headerNames));
		when(req.getHeaders("a")).thenReturn(makeEnumeration(new String[]{"1"}));
		when(req.getHeaders("b")).thenReturn(makeEnumeration(new String[]{"2", "3"}));

		HttpRequest newReq = reqTrans.make(req, "", REQUEST_TYPE.GET);

		final Set<String> headerNameSet = new HashSet<String>(Arrays.asList(headerNames));
		CollectionUtils.collect(Arrays.asList(newReq.getAllHeaders()), new Transformer() {
			@Override
			public Object transform(Object header) {
				headerNameSet.remove(((Header)header).getName());
				return null;
			}
		});
		// check header names
		Assert.assertEquals(0, headerNameSet.size());
		
		// check header values
		Assert.assertEquals(1, newReq.getHeaders("a").length);
		Assert.assertEquals("1", newReq.getHeaders("a")[0].getValue());
		
		Assert.assertEquals(2, newReq.getHeaders("b").length);
		Assert.assertEquals("2", newReq.getHeaders("b")[0].getValue());
		Assert.assertEquals("3", newReq.getHeaders("b")[1].getValue());
		
	}

	private Enumeration<String> makeEnumeration(final String[] values) {
		return new Enumeration<String>() {

			int idx = 0;

			@Override
			public boolean hasMoreElements() {
				return idx < values.length;
			}

			@Override
			public String nextElement() {
				return values[idx++];
			}
		};
	}

}

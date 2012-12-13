package com.dianping.phoenix.agent.core.task.processor.kernel.qa;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.dianping.phoenix.agent.core.TestUtil;
import com.dianping.phoenix.agent.core.task.processor.kernel.qa.QaService.CheckResult;
import com.dianping.phoenix.agent.util.url.UrlContentFetcher;

public class DefaultQaServiceTest {

	private Matcher<String> submitMatcher = new ArgumentMatcher<String>() {

		@Override
		public boolean matches(Object arg) {
			return arg != null && ((String) arg).contains("feature");
		}

	};

	private Matcher<String> queryMatcher = new ArgumentMatcher<String>() {

		@Override
		public boolean matches(Object arg) {
			return arg != null && ((String) arg).contains("token");
		}
	};

	@Test
	public void testSubmit() throws Exception {
		DefaultQaService qaService = new DefaultQaService();
		UrlContentFetcher mockUrlContentFetcher = mock(UrlContentFetcher.class);
		TestUtil.replaceFieldByReflection(qaService, "urlContentFetcher", mockUrlContentFetcher);

		// exception when get url content
		// if use any(), thenThrow() will override thenReturn()
		when(mockUrlContentFetcher.fetchUrlContent(argThat(submitMatcher))).thenThrow(
				new IOException("fake exception"));
		CheckResult checkResult = qaService.isDomainHealthy(mock(DomainHealthCheckInfo.class), 1000, 1000);
		Assert.assertEquals(CheckResult.SUBMIT_FAILED, checkResult);

		// qa return null
		checkResult = qaService.isDomainHealthy(mock(DomainHealthCheckInfo.class), 1000, 1000);
		Assert.assertEquals(CheckResult.SUBMIT_FAILED, checkResult);

		// qa return invalid json
		when(mockUrlContentFetcher.fetchUrlContent(any(String.class))).thenReturn("invalid json");
		checkResult = qaService.isDomainHealthy(mock(DomainHealthCheckInfo.class), 1000, 1000);
		Assert.assertEquals(CheckResult.SUBMIT_FAILED, checkResult);

		// qa return valid submit failed json
		when(mockUrlContentFetcher.fetchUrlContent(any(String.class))).thenReturn(
				"{\"status\" : \"error\"}");
		checkResult = qaService.isDomainHealthy(mock(DomainHealthCheckInfo.class), 1000, 1000);
		Assert.assertEquals(CheckResult.SUBMIT_FAILED, checkResult);

		// qa return valid submit success json
		when(mockUrlContentFetcher.fetchUrlContent(any(String.class))).thenReturn(
				"{\"status\" : \"ok\"}");
		checkResult = qaService.isDomainHealthy(mock(DomainHealthCheckInfo.class), 1000, 1000);
		Assert.assertEquals(CheckResult.TIMEOUT, checkResult);

	}

	@Test
	public void testQueryPass() throws Exception {
		DefaultQaService qaService = new DefaultQaService();
		UrlContentFetcher mockUrlContentFetcher = mock(UrlContentFetcher.class);
		TestUtil.replaceFieldByReflection(qaService, "urlContentFetcher", mockUrlContentFetcher);

		when(mockUrlContentFetcher.fetchUrlContent(argThat(submitMatcher))).thenReturn(
				"{\"status\" : \"ok\"}");
		when(mockUrlContentFetcher.fetchUrlContent(argThat(queryMatcher))).thenReturn(
				"{\"status\" : \"success\"}");

		DomainHealthCheckInfo domainCheckInfo = new DomainHealthCheckInfo("", "", "", 8080, "");
		CheckResult checkResult = qaService.isDomainHealthy(domainCheckInfo, 1000, 500);
		Assert.assertEquals(CheckResult.PASS, checkResult);
	}

	@Test
	public void testQueryPending() throws Exception {
		DefaultQaService qaService = new DefaultQaService();
		UrlContentFetcher mockUrlContentFetcher = mock(UrlContentFetcher.class);
		TestUtil.replaceFieldByReflection(qaService, "urlContentFetcher", mockUrlContentFetcher);

		when(mockUrlContentFetcher.fetchUrlContent(argThat(submitMatcher))).thenReturn(
				"{\"status\" : \"ok\"}");
		when(mockUrlContentFetcher.fetchUrlContent(argThat(queryMatcher))).thenReturn(
				"{\"status\" : \"pending\"}");

		DomainHealthCheckInfo domainCheckInfo = new DomainHealthCheckInfo("", "", "", 8080, "");
		CheckResult checkResult = qaService.isDomainHealthy(domainCheckInfo, 1000, 1000);
		Assert.assertEquals(CheckResult.TIMEOUT, checkResult);
	}

	@Test
	public void testQueryPassAfterPending() throws Exception {
		DefaultQaService qaService = new DefaultQaService();
		UrlContentFetcher mockUrlContentFetcher = mock(UrlContentFetcher.class);
		TestUtil.replaceFieldByReflection(qaService, "urlContentFetcher", mockUrlContentFetcher);

		final AtomicInteger queried = new AtomicInteger(1);

		when(mockUrlContentFetcher.fetchUrlContent(argThat(submitMatcher))).thenReturn(
				"{\"status\" : \"ok\"}");
		when(mockUrlContentFetcher.fetchUrlContent(argThat(queryMatcher))).thenAnswer(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				if (queried.getAndIncrement() == 1) {
					return "{\"status\" : \"pending\"}";
				} else {
					return "{\"status\" : \"success\"}";
				}
			}
		});

		DomainHealthCheckInfo domainCheckInfo = new DomainHealthCheckInfo("", "", "", 8080, "");
		CheckResult checkResult = qaService.isDomainHealthy(domainCheckInfo, 1000, 100);
		Assert.assertEquals(CheckResult.PASS, checkResult);
	}

	@Test
	public void testQueryFailAfterPending() throws Exception {
		DefaultQaService qaService = new DefaultQaService();
		UrlContentFetcher mockUrlContentFetcher = mock(UrlContentFetcher.class);
		TestUtil.replaceFieldByReflection(qaService, "urlContentFetcher", mockUrlContentFetcher);

		final AtomicInteger queried = new AtomicInteger(1);

		when(mockUrlContentFetcher.fetchUrlContent(argThat(submitMatcher))).thenReturn(
				"{\"status\" : \"ok\"}");
		when(mockUrlContentFetcher.fetchUrlContent(argThat(queryMatcher))).thenAnswer(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				if (queried.getAndIncrement() == 1) {
					return "{\"status\" : \"pending\"}";
				} else {
					return "{\"status\" : \"fail\"}";
				}
			}
		});

		DomainHealthCheckInfo domainCheckInfo = new DomainHealthCheckInfo("", "", "", 8080, "");
		CheckResult checkResult = qaService.isDomainHealthy(domainCheckInfo, 1000, 100);
		Assert.assertEquals(CheckResult.FAIL, checkResult);
	}

}

package com.dianping.phoenix.agent.core.task.processor.kernel;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.agent.core.TestUtil;
import com.dianping.phoenix.agent.core.task.processor.kernel.qa.DomainHealthCheckInfo;
import com.dianping.phoenix.agent.core.task.processor.kernel.qa.QaService;
import com.dianping.phoenix.agent.core.task.processor.kernel.qa.QaService.CheckResult;

public class DefaultDeployStepTest extends ComponentTestCase {

	@Test
	public void testCallQaService() throws Exception {
		DefaultDeployStep steps = lookup(DefaultDeployStep.class);

		QaService qaService = mock(QaService.class);
		when(qaService.isDomainHealthy(any(DomainHealthCheckInfo.class), anyInt(), anyInt())).thenAnswer(
				new Answer<Object>() {

					@Override
					public Object answer(InvocationOnMock invocation) throws Throwable {
						Assert.fail();
						return null;
					}
				});
		TestUtil.replaceFieldByReflection(steps, "qaService", qaService);

		// invalid qa service url, won't call
		DeployTask task = new DeployTask("domain", "kernelVersion", null);
		steps.prepare(task, mock(OutputStream.class));
		steps.checkContainerStatus();

		task = new DeployTask("domain", "kernelVersion", "");
		steps.prepare(task, mock(OutputStream.class));
		steps.checkContainerStatus();

		task = new DeployTask("domain", "kernelVersion", "  \n\r\t ");
		steps.prepare(task, mock(OutputStream.class));
		steps.checkContainerStatus();

		qaService = mock(QaService.class);
		final AtomicBoolean qaServiceCalled = new AtomicBoolean(false);
		when(qaService.isDomainHealthy(any(DomainHealthCheckInfo.class), anyInt(), anyInt())).thenAnswer(
				new Answer<CheckResult>() {

					@Override
					public CheckResult answer(InvocationOnMock invocation) throws Throwable {
						qaServiceCalled.set(true);
						return CheckResult.PASS;
					}
				});
		TestUtil.replaceFieldByReflection(steps, "qaService", qaService);

		// valid qa service url, will call
		task = new DeployTask("domain", "kernelVersion", "url");
		steps.prepare(task, mock(OutputStream.class));
		steps.checkContainerStatus();

		Assert.assertTrue(qaServiceCalled.get());

	}

	@Test
	public void testCallQaServiceAndResult() throws Exception {
		DefaultDeployStep steps = lookup(DefaultDeployStep.class);

		QaService qaService = mock(QaService.class);
		final AtomicReference<CheckResult> mockCheckResultRef = new AtomicReference<QaService.CheckResult>();
		when(qaService.isDomainHealthy(any(DomainHealthCheckInfo.class), anyInt(), anyInt())).thenAnswer(
				new Answer<CheckResult>() {

					@Override
					public CheckResult answer(InvocationOnMock invocation) throws Throwable {
						return mockCheckResultRef.get();
					}
				});
		TestUtil.replaceFieldByReflection(steps, "qaService", qaService);

		DeployTask task = new DeployTask("domain", "kernelVersion", "url");
		steps.prepare(task, mock(OutputStream.class));

		CheckResult[] stepErrorResults = new CheckResult[] { CheckResult.AGENT_LOCAL_EXCEPTION, //
				CheckResult.FAIL, //
				CheckResult.QA_LOCAL_EXCEPTION, //
				CheckResult.SUBMIT_FAILED, //
				CheckResult.FAIL //
		};

		CheckResult[] stepOkResults = new CheckResult[] { CheckResult.PASS };

		for (int i = 0; i < stepErrorResults.length; i++) {
			mockCheckResultRef.set(stepErrorResults[i]);
			int exitCode = steps.checkContainerStatus();
			Assert.assertEquals(DeployStep.CODE_ERROR, exitCode);
		}

		for (int i = 0; i < stepOkResults.length; i++) {
			mockCheckResultRef.set(stepOkResults[i]);
			int exitCode = steps.checkContainerStatus();
			Assert.assertEquals(DeployStep.CODE_OK, exitCode);
		}

	}

	@Test
	public void testCallQaServiceException() throws Exception {
		DefaultDeployStep steps = lookup(DefaultDeployStep.class);

		QaService qaService = mock(QaService.class);
		final AtomicBoolean qaServiceCalled = new AtomicBoolean(false);
		when(qaService.isDomainHealthy(any(DomainHealthCheckInfo.class), anyInt(), anyInt())).thenAnswer(
				new Answer<Object>() {

					@Override
					public Object answer(InvocationOnMock invocation) throws Throwable {
						qaServiceCalled.set(true);
						throw new RuntimeException("fake exception");
					}
				});
		TestUtil.replaceFieldByReflection(steps, "qaService", qaService);

		DeployTask task = new DeployTask("domain", "kernelVersion", "url");
		steps.prepare(task, mock(OutputStream.class));
		int exitCode = steps.checkContainerStatus();
		Assert.assertTrue(qaServiceCalled.get());
		Assert.assertEquals(DeployStep.CODE_ERROR, exitCode);
	}

}

package com.dianping.phoenix.agent.core.task.processor.kernel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.agent.core.tx.LogFormatter;
import com.dianping.phoenix.agent.core.tx.LogFormatterTest.LogState;

@SuppressWarnings("unchecked")
public class DeployWorkflowTest extends ComponentTestCase {

	private final int STEP_COUNT_OF_WORKFLOW = 10;

	@Test
	public void testLogFormat() throws Exception {
		DeployWorkflow workflow = lookup(DeployWorkflow.class);
		DeployStep steps = lookup(DeployStep.class);
		ByteArrayOutputStream logOut = new ByteArrayOutputStream();
		workflow.start(new DeployTask("domain", "kernelVersion", ""), steps, logOut, lookup(LogFormatter.class));
		List<String> logLines = IOUtils.readLines(new ByteArrayInputStream(logOut.toByteArray()));
		
		checkLogFormat(logLines);

	}
	
	private void checkLogFormat(List<String> logLines) {
		LogState state = LogState.READING_HEADER;
		boolean someChunkGot = false;
		boolean progress100 = false;
		boolean finalStatusGot = false;
		boolean someStepGot = false;

		for (String line : logLines) {
			switch (state) {
			case READING_HEADER:
				if ("".equals(line)) {
					state = LogState.READING_CHUNK;
				} else if (LogFormatter.LOG_TERMINATOR.trim().equals(line)) {
					state = LogState.END;
				} else if (LogFormatter.CHUNK_TERMINATOR.trim().equals(line)) {
					state = LogState.READING_HEADER;
				} else {
					if (!line.matches("[^:]+:.+")) {
						Assert.fail(String.format("log header format invalid %s", line));
					}
					if (line.matches("Progress: .*/100$")) {
						progress100 = true;
					} else if (line.equals("Status: successful") || line.equals("Status: failed")) {
						finalStatusGot = true;
					} else if (line.startsWith("Step: ")) {
						someStepGot = true;
					}

				}
				break;

			case READING_CHUNK:
				if (LogFormatter.CHUNK_TERMINATOR.trim().equals(line)) {
					state = LogState.READING_HEADER;
				}
				someChunkGot = true;
				break;

			case END:
				Assert.fail();
				break;

			}
		}

		Assert.assertTrue(someChunkGot);
		Assert.assertTrue(progress100);
		Assert.assertTrue(finalStatusGot);
		Assert.assertTrue(someStepGot);
	}

	@Test
	public void testEncounterExceptionInAnyStep() throws Exception {
		for (int i = 0; i < STEP_COUNT_OF_WORKFLOW; i++) {
			DeployWorkflow workflow = lookup(DeployWorkflow.class);
			MockDeployStep steps = (MockDeployStep) lookup(DeployStep.class);
			steps.setThrowExceptionAtStep(i);
			ByteArrayOutputStream logOut = new ByteArrayOutputStream();
			int exitCode = workflow.start(new DeployTask("domain", "kernelVersion", ""), steps, logOut, mock(LogFormatter.class));
			Assert.assertTrue(exitCode != DeployStep.CODE_OK);
		}

	}

	@Test
	public void testReturnErrorInAnyStep() throws Exception {
		for (int i = 0; i < STEP_COUNT_OF_WORKFLOW; i++) {
			DeployWorkflow workflow = lookup(DeployWorkflow.class);
			MockDeployStep steps = (MockDeployStep) lookup(DeployStep.class);
			steps.setReturnErrorCodeAtStep(i);
			ByteArrayOutputStream logOut = new ByteArrayOutputStream();
			int exitCode = workflow.start(new DeployTask("domain", "kernelVersion", ""), steps, logOut, mock(LogFormatter.class));
			Assert.assertTrue(exitCode != DeployStep.CODE_OK);
		}

	}

	@Test
	public void testKill() throws Exception {
		final DeployStep steps = mock(DeployStep.class);

		final CountDownLatch entryLatch = new CountDownLatch(1);
		final CountDownLatch exitLatch = new CountDownLatch(1);
		when(steps.stopAll()).then(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				// ensure we are in step stopContainer and let kill() happen
				entryLatch.countDown();

				// wait until kill() is called
				boolean awaitOk = exitLatch.await(2, TimeUnit.SECONDS);
				if (!awaitOk) {
					Assert.fail();
				}
				return null;
			}
		});

		// stopContainer is the preceding step of upgradeKernel
		when(steps.upgradeKernel()).then(new Answer<Object>() {

			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				// won't call this because workflow has been killed in preceding step stopContainer
				Assert.fail();
				return null;
			}
		});

		final DeployWorkflow workflow = lookup(DeployWorkflow.class);
		final ByteArrayOutputStream logOut = new ByteArrayOutputStream();
		new Thread() {
			public void run() {
				workflow.start(new DeployTask("domain", "kernelVersion", ""), steps, logOut, mock(LogFormatter.class));
			}
		}.start();

		// wait until step stopContainer
		boolean awaitOk = entryLatch.await(2, TimeUnit.SECONDS);
		if (!awaitOk) {
			Assert.fail();
		}

		workflow.kill();

		// let stopContainer continue
		exitLatch.countDown();

	}

}

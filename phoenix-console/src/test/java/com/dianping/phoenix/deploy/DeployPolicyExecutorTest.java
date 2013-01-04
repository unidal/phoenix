package com.dianping.phoenix.deploy;

import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.deploy.agent.AgentProgress;
import com.dianping.phoenix.deploy.agent.AgentReader;

public class DeployPolicyExecutorTest extends ComponentTestCase {
	@Test
	public void testConfigure() throws Exception {
		// all executors should be configured
		for (DeployPolicy policy : DeployPolicy.values()) {
			DeployExecutor executor = lookup(DeployExecutor.class, policy.getId());

			Assert.assertSame(policy, executor.getPolicy());
		}
	}

	@Test
	public void testProtocol() throws Exception {
		InputStream in = getClass().getResourceAsStream("protocol.txt");
		AgentReader reader = new AgentReader(new InputStreamReader(in, "utf-8"));
		AgentProgress progress = new AgentProgress();
		StringBuilder sb = new StringBuilder(4096);

		while (reader.hasNext()) {
			String segment = reader.next(progress);

			sb.append(segment);
		}

		Assert.assertEquals("Progress[100/100, failed, rollback]", progress.toString());
		Assert.assertEquals(1977, sb.length());
	}
}

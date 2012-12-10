package com.dianping.phoenix.deploy;

import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.deploy.agent.Progress;
import com.dianping.phoenix.deploy.agent.SegmentReader;

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
		SegmentReader reader = new SegmentReader(new InputStreamReader(in, "utf-8"));
		Progress progress = new Progress();
		StringBuilder sb = new StringBuilder(4096);

		while (reader.hasNext()) {
			String segment = reader.next(progress);

			sb.append(segment);
		}

		Assert.assertEquals("Progress[100/100, failed, rollback]", progress.toString());
		Assert.assertEquals(1977, sb.length());
	}
}

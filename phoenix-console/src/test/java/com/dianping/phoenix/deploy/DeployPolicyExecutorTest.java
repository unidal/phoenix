package com.dianping.phoenix.deploy;

import junit.framework.Assert;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class DeployPolicyExecutorTest extends ComponentTestCase {
	@Test
	public void testConfigure() throws Exception {
		// all executors should be configured
		for (DeployPolicy policy : DeployPolicy.values()) {
			DeployExecutor executor = lookup(DeployExecutor.class, policy.getId());
			
			Assert.assertSame(policy, executor.getPolicy());
		}
	}
}

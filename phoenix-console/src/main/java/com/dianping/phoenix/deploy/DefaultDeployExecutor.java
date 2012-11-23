package com.dianping.phoenix.deploy;

import java.util.List;

import org.unidal.lookup.annotation.Inject;

public class DefaultDeployExecutor implements DeployExecutor {
	@Inject
	private DeployPolicy m_policy;

	@Override
	public int submit(String name, List<String> hosts, String version, boolean abortOnError) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setPolicy(DeployPolicy policy) {
		m_policy = policy;
	}

	@Override
	public DeployPolicy getPolicy() {
		return m_policy;
	}
}

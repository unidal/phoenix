package com.dianping.phoenix.deploy;

import java.util.List;

public interface DeployExecutor {
	public DeployPolicy getPolicy();

	public void submit(int deployId, String name, List<String> hosts, String version, boolean abortOnError)
	      throws Exception;
}

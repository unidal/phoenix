package com.dianping.phoenix.deploy;

import java.util.List;

public interface DeployExecutor {
	public DeployPolicy getPolicy();

	public void submit(int deployId, List<String> hosts) throws Exception;
}

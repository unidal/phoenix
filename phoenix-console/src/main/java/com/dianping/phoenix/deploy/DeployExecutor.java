package com.dianping.phoenix.deploy;

import java.util.List;

public interface DeployExecutor {
	public DeployPolicy getPolicy();

	public DeployUpdate poll(DeployContext ctx);

	public void submit(int deployId, String name, List<String> hosts, String version, boolean abortOnError);
}

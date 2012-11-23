package com.dianping.phoenix.deploy;

import java.util.List;

public interface DeployExecutor {
	public DeployPolicy getPolicy();

	public int submit(String name, List<String> hosts, String version, boolean abortOnError);
}

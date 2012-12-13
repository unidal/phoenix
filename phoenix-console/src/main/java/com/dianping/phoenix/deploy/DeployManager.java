package com.dianping.phoenix.deploy;

import java.util.List;

public interface DeployManager {
	public int deploy(String name, List<String> hosts, DeployPlan plan) throws Exception;
}

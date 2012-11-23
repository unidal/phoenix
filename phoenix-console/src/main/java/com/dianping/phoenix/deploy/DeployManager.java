package com.dianping.phoenix.deploy;

import java.util.List;

import com.dianping.phoenix.console.dal.deploy.Deployment;

public interface DeployManager {
	public int deploy(String name, List<String> hosts, DeployPlan plan) throws Exception;

	public Deployment query(int deployId) throws Exception;
}

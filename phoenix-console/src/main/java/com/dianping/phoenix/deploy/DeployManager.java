package com.dianping.phoenix.deploy;

import java.util.List;

import com.dianping.phoenix.deploy.model.entity.DeployModel;

public interface DeployManager {
	public int deploy(String name, List<String> hosts, DeployPlan plan) throws Exception;

	public DeployModel getModel(int deployId);
}

package com.dianping.phoenix.deploy;

import java.util.List;

import com.dianping.phoenix.deploy.model.entity.DeployModel;

public interface DeployExecutor {
	public DeployPolicy getPolicy();

	public DeployUpdate poll(DeployContext ctx);

	public void submit(int deployId, String name, List<String> hosts, String version, boolean abortOnError);

	public DeployModel getModel(int deployId);
}

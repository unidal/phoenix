package com.dianping.phoenix.deploy;

import java.util.List;

import com.dianping.phoenix.deploy.model.entity.DeployModel;

public interface DeployExecutor {
	public DeployPolicy getPolicy();

	public void submit(DeployModel model, List<String> hosts, String warType, String logUri) throws Exception;
}

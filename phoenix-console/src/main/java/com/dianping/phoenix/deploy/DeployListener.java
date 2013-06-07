package com.dianping.phoenix.deploy;

import java.util.List;

import com.dianping.phoenix.deploy.model.entity.DeployModel;

public interface DeployListener {
	public DeployModel onCreate(String domain, List<String> hosts, DeployPlan plan) throws Exception;

	public void onDeployEnd(int deployId) throws Exception;

	public void onDeployStart(int deployId) throws Exception;

	public void onDeployPause(int deployId) throws Exception;

	public void onDeployCancel(int deployId) throws Exception;

	public void onHostCancel(int deployId, String host) throws Exception;

	public void onHostEnd(int deployId, String host) throws Exception;

	public void onDeployContinue(int deployId) throws Exception;
}

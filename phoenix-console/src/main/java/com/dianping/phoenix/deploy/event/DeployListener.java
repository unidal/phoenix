package com.dianping.phoenix.deploy.event;

import java.util.List;

import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.deploy.model.entity.DeployModel;

public interface DeployListener {
	public DeployModel getModel(int deployId);

	public DeployModel onCreate(String name, List<String> hosts, DeployPlan plan) throws Exception;

	public void onDeployEnd(int deployId) throws Exception;

	public void onDeployStart(int deployId) throws Exception;

	public void onHostCancel(int deployId, String host) throws Exception;
	
	public void onHostEnd(int deployId, String host) throws Exception;
}

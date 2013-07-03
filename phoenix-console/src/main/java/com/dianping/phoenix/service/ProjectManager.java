package com.dianping.phoenix.service;

import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.deploy.model.entity.DeployModel;

public interface ProjectManager {
	public Deployment findActiveDeploy(String type, String name);

	public DeployModel findModel(int deployId);

	public void storeModel(DeployModel model);
}

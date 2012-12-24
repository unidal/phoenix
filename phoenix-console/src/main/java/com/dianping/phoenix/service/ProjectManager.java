package com.dianping.phoenix.service;

import java.util.List;

import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.project.entity.Project;

public interface ProjectManager {
	public Deployment findActiveDeploy(String name);

	public DeployModel findModel(int deployId);

	public Project findProjectBy(String name) throws Exception;

	public List<Project> searchProjects(String keyword) throws Exception;

	public void storeModel(DeployModel model);
}

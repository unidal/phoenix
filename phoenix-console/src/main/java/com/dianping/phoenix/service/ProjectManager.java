package com.dianping.phoenix.service;

import java.util.List;

import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.deploy.entity.Project;

public interface ProjectManager {
	public List<Project> searchProjects(String keyword) throws Exception;

	public Project findProjectBy(String name) throws Exception;

	public int deployToProject(String name, List<String> hosts, DeployPlan plan) throws Exception;
}

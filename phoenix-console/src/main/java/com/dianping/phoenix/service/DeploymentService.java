package com.dianping.phoenix.service;

import java.util.List;

import com.dianping.phoenix.deploy.entity.Project;

public interface DeploymentService {
	public List<Project> search(String domain, String keyword);

	public Project findByName(String project);

	public int deploy(List<String> hosts, DeploymentPlan plan);

}

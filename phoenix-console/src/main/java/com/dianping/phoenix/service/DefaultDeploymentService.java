package com.dianping.phoenix.service;

import java.util.List;

import com.dianping.phoenix.deploy.entity.Project;

public class DefaultDeploymentService implements DeploymentService {
	@Override
   public List<Project> search(String domain, String keyword) {
	   // TODO Auto-generated method stub
	   return null;
   }

	@Override
   public Project findByName(String name) {
	   // TODO Auto-generated method stub
	   return null;
   }

	@Override
   public int deploy(List<String> hosts, DeploymentPlan plan) {
	   // TODO Auto-generated method stub
	   return 0;
   }
}

package com.dianping.phoenix.console.service;

import java.util.List;

public interface DeployService {
	public boolean deploy(List<String> hosts, String id);

	public List<HostPlan> getHostPlans(String id);

	public int getMessages(String id, int offset, StringBuilder sb);

	public String getStatus(String id);
}
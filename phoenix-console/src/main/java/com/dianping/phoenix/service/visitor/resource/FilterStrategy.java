package com.dianping.phoenix.service.visitor.resource;

import java.util.Set;

import com.dianping.phoenix.agent.resource.entity.Resource;

public interface FilterStrategy {

	public Set<String> getStrategy();

	public Resource getResource();
}

package com.dianping.phoenix.service.resource.netty;

import java.util.List;

import com.dianping.phoenix.agent.resource.entity.Host;


public interface AgentStatusFetcher {
	public void fetchPhoenixAgentStatus(List<Host> hosts);
}

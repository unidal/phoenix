package com.dianping.phoenix.agent.core;

import com.dianping.phoenix.agent.response.entity.Response;

public interface AgentStatusHolder {
	public Response getAgentStatusResponse();
	public void onAgentStatusChanged();
}

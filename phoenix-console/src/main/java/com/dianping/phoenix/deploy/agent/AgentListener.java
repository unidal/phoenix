package com.dianping.phoenix.deploy.agent;

public interface AgentListener {
	public void onProgress(AgentContext ctx, AgentProgress progress, String log) throws Exception;

	public void onStart(AgentContext ctx) throws Exception;

	public void onEnd(AgentContext ctx, AgentStatus status) throws Exception;
}

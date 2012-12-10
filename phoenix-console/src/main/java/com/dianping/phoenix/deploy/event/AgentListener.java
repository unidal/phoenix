package com.dianping.phoenix.deploy.event;

import com.dianping.phoenix.deploy.agent.Context;
import com.dianping.phoenix.deploy.agent.Progress;

public interface AgentListener {
	public void onProgress(Context ctx, Progress progress, String log) throws Exception;

	public void onStart(Context ctx) throws Exception;

	public void onEnd(Context ctx, String status) throws Exception;
}

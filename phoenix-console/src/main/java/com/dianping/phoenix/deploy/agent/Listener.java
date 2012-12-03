package com.dianping.phoenix.deploy.agent;

public interface Listener {
	public void onProgress(Context ctx, Progress progress, String log);

	public void onStart(Context ctx);

	public void onEnd(Context ctx, String status);
}

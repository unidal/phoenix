package com.dianping.phoenix.deploy.agent;

import java.io.IOException;

import com.dianping.phoenix.configure.ConfigManager;

public interface Context {
	public ConfigManager getConfigManager();

	public int getDeployId();

	public String getDomain();

	public String getHost();

	public int getId();

	public String getRawLog();

	public int getRetryCount();

	public State getState();

	public String getVersion();
	
	public String openUrl(String url) throws IOException;

	public Context print(String string, Object... args);

	public Context println();

	public Context println(String string, Object... args);

	public void setRetryCount(int retryCount);

	public void setState(State state);

	public void updateStatus(String status, String message);
}
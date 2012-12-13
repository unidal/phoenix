package com.dianping.phoenix.deploy.agent;

import java.io.IOException;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.deploy.model.entity.DeployModel;

public interface Context {
	public ConfigManager getConfigManager();

	public int getDeployId();

	public DeployModel getDeployModel();

	public String getDomain();

	public String getHost();

	public int getId();

	public String getRawLog();

	public int getRetriedCount();

	public State getState();

	public String getVersion();

	public boolean isFailed();

	public String openUrl(String url) throws IOException;

	public Context print(String string, Object... args);

	public Context println();

	public Context println(String string, Object... args);

	public void setFailed(boolean failed);

	public void setRetriedCount(int retriedCount);

	public void setState(State state);

	public void updateStatus(String status, String message);
}
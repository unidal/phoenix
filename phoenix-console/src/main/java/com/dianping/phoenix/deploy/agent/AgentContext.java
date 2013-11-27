package com.dianping.phoenix.deploy.agent;

import java.io.IOException;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.deploy.DeployType;
import com.dianping.phoenix.deploy.model.entity.DeployModel;

public interface AgentContext {
	public ConfigManager getConfigManager();

	public int getDeployId();

	public DeployModel getDeployModel();

	public String getDomain();

	public String getHost();

	public int getId();

	public String getRawLog();

	public int getRetriedCount();

	public AgentState getState();

	public AgentStatus getStatus();

	public String getVersion();

	public DeployType getWarType();

	public boolean isSkipTest();

	public String openUrl(String url) throws IOException;

	public AgentContext print(String pattern, Object... args);

	public AgentContext println();

	public AgentContext println(String pattern, Object... args);

	public void setRetriedCount(int retriedCount);

	public void setState(AgentState state);

	public void updateStatus(AgentStatus status, String message);
	
	public void refreshInternalInformation();
}
package com.dianping.phoenix.agent.core.task.processor.kernel.upgrade;

import java.io.OutputStream;

import com.dianping.phoenix.agent.core.task.processor.kernel.DeployTask;

public interface KernelUpgradeStepProvider {
	int prepare(DeployTask task, OutputStream logOut);

	int init() throws Exception;

	int checkArgument() throws Exception;

	int injectPhoenixLoader() throws Exception;

	int getKernelWar() throws Exception;

	int stopAll() throws Exception;

	int upgradeKernel() throws Exception;

	int startContainer() throws Exception;

	int checkContainerStatus() throws Exception;

	int commit() throws Exception;

	int rollback() throws Exception;

	void kill();
}

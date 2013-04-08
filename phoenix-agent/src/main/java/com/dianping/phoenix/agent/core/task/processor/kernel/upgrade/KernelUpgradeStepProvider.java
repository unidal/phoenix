package com.dianping.phoenix.agent.core.task.processor.kernel.upgrade;

import com.dianping.phoenix.agent.core.task.workflow.Context;

public interface KernelUpgradeStepProvider {

	int init(Context ctx) throws Exception;

	int checkArgument(Context ctx) throws Exception;

	int injectPhoenixLoader(Context ctx) throws Exception;

	int getKernelWar(Context ctx) throws Exception;

	int stopAll(Context ctx) throws Exception;

	int upgradeKernel(Context ctx) throws Exception;

	int startContainer(Context ctx) throws Exception;

	int checkContainerStatus(Context ctx) throws Exception;

	int commit(Context ctx) throws Exception;

	int rollback(Context ctx) throws Exception;
}

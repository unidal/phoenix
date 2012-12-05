package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.OutputStream;

public interface DeployStep {

	public final int CODE_OK = 0;
	public final int CODE_ERROR = -1;

	void prepare(String domain, String kernelVersion, OutputStream logOut);

	int start() throws Exception;

	int init() throws Exception;

	int checkArgument() throws Exception;

	int injectPhoenixLoader() throws Exception;

	int getKernelWar() throws Exception;

	int turnOffTraffic() throws Exception;

	int stopContainer() throws Exception;

	int upgradeKernel() throws Exception;

	int startContainer() throws Exception;
	
	int checkContainerStatus() throws Exception;

	int commit() throws Exception;

	int rollback() throws Exception;

	void kill();


}

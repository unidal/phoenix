package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.OutputStream;

public class MockDeployStep implements DeployStep {

	private int throwExceptionAtStep = -1;
	private int returnErrorCodeAtStep = -1;

	private int step = -1;

	public void setThrowExceptionAtStep(int throwExceptionAtStep) {
		this.throwExceptionAtStep = throwExceptionAtStep;
	}

	public void setReturnErrorCodeAtStep(int returnErrorCodeAtStep) {
		this.returnErrorCodeAtStep = returnErrorCodeAtStep;
	}

	private int mockActivity() throws Exception {
		step++;
		if (throwExceptionAtStep == step) {
			throw new Exception("fake exception");
		}
		int code = DeployStep.CODE_OK;
		if (returnErrorCodeAtStep == step) {
			code = DeployStep.CODE_ERROR;
		}
		return code;
	}

	@Override
	public void prepare(String domain, String kernelVersion, OutputStream logOut) {
	}

	@Override
	public int init() throws Exception {
		return mockActivity();
	}

	@Override
	public int checkArgument() throws Exception {
		return mockActivity();
	}

	@Override
	public int injectPhoenixLoader() throws Exception {
		return mockActivity();
	}

	@Override
	public int getKernelWar() throws Exception {
		return mockActivity();
	}

	@Override
	public int turnOffTraffic() throws Exception {
		return mockActivity();
	}

	@Override
	public int stopContainer() throws Exception {
		return mockActivity();
	}

	@Override
	public int upgradeKernel() throws Exception {
		return mockActivity();
	}

	@Override
	public int startContainer() throws Exception {
		return mockActivity();
	}

	@Override
	public int checkContainerStatus() throws Exception {
		return mockActivity();
	}

	@Override
	public int commit() throws Exception {
		return mockActivity();
	}

	@Override
	public int rollback() throws Exception {
		return mockActivity();
	}

	@Override
	public void kill() {
	}

}

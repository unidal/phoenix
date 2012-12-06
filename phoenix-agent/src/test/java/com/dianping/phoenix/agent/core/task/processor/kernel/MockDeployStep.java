package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.OutputStream;

public class MockDeployStep implements DeployStep {

	private int throwExceptionAtStep = -1;
	private int returnErrorCodeAtStep = -1;

	private int step = -1;
	
	private OutputStream logOut;

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
		this.logOut = logOut;
	}

	@Override
	public int init() throws Exception {
		logOut.write("mock init".getBytes());
		return mockActivity();
	}

	@Override
	public int checkArgument() throws Exception {
		logOut.write("mock checkArgument".getBytes());
		return mockActivity();
	}

	@Override
	public int injectPhoenixLoader() throws Exception {
		logOut.write("mock injectPhoenixLoader".getBytes());
		return mockActivity();
	}

	@Override
	public int getKernelWar() throws Exception {
		logOut.write("mock getKernelWar".getBytes());
		return mockActivity();
	}

	@Override
	public int turnOffTraffic() throws Exception {
		logOut.write("mock turnOffTraffic".getBytes());
		return mockActivity();
	}

	@Override
	public int stopContainer() throws Exception {
		logOut.write("mock stopContainer".getBytes());
		return mockActivity();
	}

	@Override
	public int upgradeKernel() throws Exception {
		logOut.write("mock upgradeKernel".getBytes());
		return mockActivity();
	}

	@Override
	public int startContainer() throws Exception {
		logOut.write("mock startContainer".getBytes());
		return mockActivity();
	}

	@Override
	public int checkContainerStatus() throws Exception {
		logOut.write("mock checkContainerStatus".getBytes());
		return mockActivity();
	}

	@Override
	public int commit() throws Exception {
		logOut.write("mock commit".getBytes());
		return mockActivity();
	}

	@Override
	public int rollback() throws Exception {
		logOut.write("mock rollback".getBytes());
		return mockActivity();
	}

	@Override
	public void kill() {
	}

}

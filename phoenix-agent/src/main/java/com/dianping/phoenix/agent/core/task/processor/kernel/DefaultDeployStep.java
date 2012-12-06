package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.File;
import java.io.OutputStream;
import java.net.URL;

import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.core.shell.ScriptExecutor;

public class DefaultDeployStep implements DeployStep {

	@Inject
	private ScriptExecutor scriptExecutor;
	@Inject
	private Config config;

	private OutputStream logOut;
	private String domain;
	private String kernelVersion;

	private int runShellCmd(String shellFunc) throws Exception {
		String script = jointShellCmd(domain, kernelVersion, config.getContainerType().toString(), shellFunc);
		int exitCode = scriptExecutor.exec(script, logOut, logOut);
		return exitCode;
	}

	private String jointShellCmd(String domain, String newVersion, String container, String shellFunc) {
		return String.format("%s -x \"%s\" -c \"%s\" -d \"%s\" -v \"%s\" -f \"%s\"", getScriptPath(),
				config.getServerXml(), container, domain, newVersion, shellFunc);
	}

	private void doInjectPhoenixLoader() throws Exception {
		File serverXml = config.getServerXml();
		if (serverXml == null || !serverXml.exists()) {
			throw new RuntimeException("container server.xml not found");
		}

		File kernelDocBase = new File(String.format(config.getKernelDocBasePattern(), domain, kernelVersion));
		String domainDocBasePattern = String.format(config.getDomainDocBaseFeaturePattern(), domain);
		ServerXmlUtil.attachPhoenixContextLoader(serverXml, domainDocBasePattern, config.getLoaderClass(),
				kernelDocBase);
	}

	private String getScriptPath() {
		URL scriptUrl = this.getClass().getClassLoader().getResource("agent.sh");
		if (scriptUrl == null) {
			throw new RuntimeException("agent.sh not found");
		}
		return scriptUrl.getPath();
	}

	@Override
	public void prepare(String domain, String kernelVersion, OutputStream logOut) {
		this.domain = domain;
		this.kernelVersion = kernelVersion;
		this.logOut = logOut;
	}

	@Override
	public int init() throws Exception {
		return runShellCmd("init");
	}

	@Override
	public int checkArgument() throws Exception {
		return CODE_OK;
	}

	@Override
	public int injectPhoenixLoader() throws Exception {
		int code = CODE_OK;
		try {
			doInjectPhoenixLoader();
		} catch (Exception e) {
			code = CODE_ERROR;
		}
		return code;
	}

	@Override
	public int getKernelWar() throws Exception {
		return runShellCmd("get_kernel_war");
	}

	@Override
	public int turnOffTraffic() throws Exception {
		return runShellCmd("turn_off_traffic");
	}

	@Override
	public int stopContainer() throws Exception {
		return runShellCmd("stop_container");
	}

	@Override
	public int upgradeKernel() throws Exception {
		return runShellCmd("upgrade_kernel");
	}
	
	@Override
	public int startContainer() throws Exception {
		return runShellCmd("start_container");
	}

	@Override
	public int checkContainerStatus() throws Exception {
		return runShellCmd("check_container_status");
	}

	@Override
	public int commit() throws Exception {
		return runShellCmd("commit");
	}

	@Override
	public int rollback() throws Exception {
		return runShellCmd("rollback");
	}

	@Override
	public void kill() {
		scriptExecutor.kill();
	}

}

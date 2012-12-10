package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.apache.log4j.Logger;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.util.StringUtils;

import com.dianping.cat.configuration.NetworkInterfaceManager;
import com.dianping.phoenix.agent.core.shell.ScriptExecutor;
import com.dianping.phoenix.agent.core.task.processor.kernel.qa.DomainHealthCheckInfo;
import com.dianping.phoenix.agent.core.task.processor.kernel.qa.QaService;
import com.dianping.phoenix.agent.core.task.processor.kernel.qa.QaService.CheckResult;

public class DefaultDeployStep implements DeployStep {

	private final static Logger logger = Logger.getLogger(DefaultDeployStep.class);

	@Inject
	private ScriptExecutor scriptExecutor;
	@Inject
	private Config config;
	@Inject
	private QaService qaService;

	private OutputStream logOut;
	private DeployTask task;

	private int runShellCmd(String shellFunc) throws Exception {
		String script = jointShellCmd(task.getDomain(), task.getKernelVersion(), config.getContainerType().toString(),
				shellFunc);
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

		File kernelDocBase = new File(String.format(config.getKernelDocBasePattern(), task.getDomain(),
				task.getKernelVersion()));
		String domainDocBasePattern = String.format(config.getDomainDocBaseFeaturePattern(), task.getDomain());
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
	public void prepare(DeployTask task, OutputStream logOut) {
		this.task = task;
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
		int exitCode = callQaService();
		return exitCode;

	}

	private int callQaService() throws IOException {
		String localIp = NetworkInterfaceManager.INSTANCE.getLocalHostAddress();
		String qaServiceUrlPrefix = task.getQaServiceUrlPrefix();
		DomainHealthCheckInfo deployInfo = new DomainHealthCheckInfo(task.getDomain(), config.getEnv(), localIp,
				config.getContainerPort(), qaServiceUrlPrefix);

		int exitCode = DeployStep.CODE_OK;
		if (!StringUtils.isEmpty(StringUtils.trimAll(qaServiceUrlPrefix))) {
			logger.info(String.format("qa service url is given, checking domain status via %s", qaServiceUrlPrefix));

			CheckResult checkResult = CheckResult.FAIL;
			try {
				checkResult = qaService.isDomainHealthy(deployInfo, config.getQaServiceTimeout(),
						config.getQaServiceQueryInterval());
			} catch (RuntimeException e) {
				checkResult = CheckResult.AGENT_LOCAL_EXCEPTION;
				logger.error("agent local exception when calling qa service", e);
			}
			
			logger.info(String.format("qa service check result %s", checkResult));
			exitCode = qaCheckResultToExitCode(checkResult);
		} else {
			logger.info("qa service url is not given, skip checking domain status");
		}
		return exitCode;
	}

	private int qaCheckResultToExitCode(CheckResult checkResult) {
		int exitCode;
		switch (checkResult) {

		case PASS:
			exitCode = DeployStep.CODE_OK;
			break;

		// TODO
		case TIMEOUT:
		case AGENT_LOCAL_EXCEPTION:
		case QA_LOCAL_EXCEPTION:
		case SUBMIT_FAILED:
		case FAIL:
			exitCode = DeployStep.CODE_ERROR;
			break;

		default:
			logger.error(String.format("unexpected CheckResult type %s, treat as %s", checkResult,
					DeployStep.CODE_ERROR));
			exitCode = DeployStep.CODE_ERROR;
			break;

		}
		return exitCode;
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

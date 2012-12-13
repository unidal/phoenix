package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.util.StringUtils;

import com.dianping.cat.configuration.NetworkInterfaceManager;
import com.dianping.phoenix.agent.core.shell.ScriptExecutor;
import com.dianping.phoenix.agent.core.task.processor.kernel.qa.DomainHealthCheckInfo;
import com.dianping.phoenix.agent.core.task.processor.kernel.qa.QaService;
import com.dianping.phoenix.agent.core.task.processor.kernel.qa.QaService.CheckResult;
import com.dianping.phoenix.configure.ConfigManager;

public class DefaultDeployStep implements DeployStep {

	private final static Logger logger = Logger.getLogger(DefaultDeployStep.class);

	@Inject
	private ScriptExecutor scriptExecutor;
	@Inject
	private ConfigManager config;
	@Inject
	private QaService qaService;

	private OutputStream logOut;
	private DeployTask task;

	private int runShellCmd(String shellFunc) throws Exception {
		String script = jointShellCmd(shellFunc);
		int exitCode = scriptExecutor.exec(script, logOut, logOut);
		return exitCode;
	}

	private String jointShellCmd(String shellFunc) {
		StringBuilder sb = new StringBuilder();
		
		String kernelDocBase = String.format(config.getKernelDocBasePattern(), task.getDomain(),
				task.getKernelVersion());
		
		String kernelGitUrl = task.getKernelGitUrl();
		String kernelGitHost = null;
		try {
			kernelGitHost = new URI(kernelGitUrl).getHost();
		} catch (URISyntaxException e) {
			throw new RuntimeException(String.format("error parsing host from kernel git url %s",
					kernelGitUrl), e);
		}

		sb.append(getScriptPath());
		sb.append(String.format(" -b \"%s\" ", config.getContainerInstallPath()));
		sb.append(String.format(" -x \"%s\" ", config.getServerXml()));
		sb.append(String.format(" -c \"%s\" ", config.getContainerType().toString()));
		sb.append(String.format(" -d \"%s\" ", task.getDomain()));
		sb.append(String.format(" -v \"%s\" ", task.getKernelVersion()));
		sb.append(String.format(" -k \"%s\" ", kernelDocBase));
		sb.append(String.format(" -e \"%s\" ", config.getEnv()));
		sb.append(String.format(" -g \"%s\" ", kernelGitUrl));
		sb.append(String.format(" -h \"%s\" ", kernelGitHost));
		sb.append(String.format(" -f \"%s\" ", shellFunc));

		return sb.toString();
	}

	private void doInjectPhoenixLoader() throws Exception {
		File serverXml = config.getServerXml();
		if (serverXml == null || !serverXml.exists()) {
			String path = serverXml == null ? null : serverXml.getAbsolutePath();
			throw new RuntimeException(String.format("container server.xml not found %s", path));
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
	public int stopAll() throws Exception {
		return runShellCmd("stop_all");
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
				checkResult = qaService.isDomainHealthy(deployInfo, task.getQaServiceTimeout(),
						config.getQaServiceQueryInterval());
			} catch (RuntimeException e) {
				checkResult = CheckResult.AGENT_LOCAL_EXCEPTION;
				logger.error("agent local exception when calling qa service", e);
			}

			logger.info(String.format("qa service check result %s", checkResult));
			exitCode = qaCheckResultToExitCode(checkResult);
		} else {
			logger.warn("qa service url is not given, skip checking domain status");
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

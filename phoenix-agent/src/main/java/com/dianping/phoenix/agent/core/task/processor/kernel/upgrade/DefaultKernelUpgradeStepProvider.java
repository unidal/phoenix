package com.dianping.phoenix.agent.core.task.processor.kernel.upgrade;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;
import org.unidal.lookup.util.StringUtils;

import com.dianping.cat.configuration.NetworkInterfaceManager;
import com.dianping.phoenix.agent.core.task.processor.kernel.DeployTask;
import com.dianping.phoenix.agent.core.task.processor.kernel.ServerXmlManager;
import com.dianping.phoenix.agent.core.task.processor.kernel.qa.DomainHealthCheckInfo;
import com.dianping.phoenix.agent.core.task.processor.kernel.qa.QaService;
import com.dianping.phoenix.agent.core.task.processor.kernel.qa.QaService.CheckResult;
import com.dianping.phoenix.agent.core.task.workflow.Context;
import com.dianping.phoenix.agent.core.task.workflow.Step;
import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.configure.ConfigManager.ContainerType;

public class DefaultKernelUpgradeStepProvider extends ContainerHolder implements KernelUpgradeStepProvider {
	private static final Logger logger = Logger.getLogger(DefaultKernelUpgradeStepProvider.class);

	@Inject
	private ConfigManager config;
	
	@Inject
	private QaService qaService;

	@Inject
	private ServerXmlManager m_serverXmlManager;
	
	private int runShellCmd(String shellFunc, Context ctx) throws Exception {
		KernelUpgradeContext myCtx = (KernelUpgradeContext) ctx;
		String script = jointShellCmd(shellFunc, (DeployTask) myCtx.getTask());
		int exitCode = myCtx.getScriptExecutor().exec(script, myCtx.getLogOut(), myCtx.getLogOut());
		return exitCode;
	}

	private String jointShellCmd(String shellFunc, DeployTask task) {
		StringBuilder sb = new StringBuilder();
		String kernelDocBase = String.format(config.getKernelDocBasePattern(), task.getDomain(),
				task.getKernelVersion());

		String kernelGitUrl = task.getKernelGitUrl();
		String kernelGitHost = null;
		try {
			kernelGitHost = new URI(kernelGitUrl).getHost();
		} catch (URISyntaxException e) {
			throw new RuntimeException(String.format("error parsing host from kernel git url %s", kernelGitUrl), e);
		}

		sb.append(config.getAgentScriptFile().getAbsolutePath());
		sb.append(String.format(" --container_install_path \"%s\" ", config.getContainerInstallPath()));
		sb.append(String.format(" --server_xml \"%s\" ", config.getServerXml()));
		sb.append(String.format(" --container_type \"%s\" ", config.getContainerType().toString()));
		sb.append(String.format(" --domain \"%s\" ", task.getDomain()));
		sb.append(String.format(" --kernel_version \"%s\" ", task.getKernelVersion()));
		sb.append(String.format(" --domain_kernel_base \"%s\" ", kernelDocBase));
		sb.append(String.format(" --env \"%s\" ", config.getEnv()));
		sb.append(String.format(" --kernel_git_url \"%s\" ", kernelGitUrl));
		sb.append(String.format(" --kernel_git_host \"%s\" ", kernelGitHost));
		sb.append(String.format(" --func \"%s\" ", shellFunc));

		return sb.toString();
	}

	private void doInjectPhoenixLoader(Context ctx) throws Exception {
		DeployTask task = (DeployTask) ((KernelUpgradeContext) ctx).getTask();
		ContainerType type = config.getContainerType();

		if (type == ContainerType.TOMCAT) {
			File serverXmlDir = new File(config.getContainerInstallPath() + "/conf/Catalina/localhost/");
			if (serverXmlDir != null && serverXmlDir.exists()) {
				for (File serverXml : serverXmlDir.listFiles()) {
					attachPhoenixContextLoader(task, serverXml);
				}
			}
		}

		File serverXml = config.getServerXml();
		if (serverXml == null || !serverXml.exists()) {
			String path = serverXml == null ? null : serverXml.getAbsolutePath();
			throw new RuntimeException(String.format("container server.xml not found %s", path));
		}

		attachPhoenixContextLoader(task, serverXml);
	}

	private void attachPhoenixContextLoader(DeployTask task, File serverXml) throws Exception {
		File kernelDocBase = new File(String.format(config.getKernelDocBasePattern(), task.getDomain(),
				task.getKernelVersion()));
		String domainDocBasePattern = String.format(config.getDomainDocBaseFeaturePattern(), task.getDomain());
		m_serverXmlManager.attachPhoenixContextLoader(serverXml, domainDocBasePattern, config.getLoaderClass(),
				kernelDocBase);
	}

	@Override
	public int init(Context ctx) throws Exception {
		return runShellCmd("init", ctx);
	}

	@Override
	public int checkArgument(Context ctx) throws Exception {
		return Step.CODE_OK;
	}

	@Override
	public int injectPhoenixLoader(Context ctx) throws Exception {
		int code = Step.CODE_OK;
		try {
			doInjectPhoenixLoader(ctx);
		} catch (Exception e) {
			code = Step.CODE_ERROR;
			logger.error("error inject phoenix loader", e);
		}
		return code;
	}

	@Override
	public int getKernelWar(Context ctx) throws Exception {
		return runShellCmd("get_kernel_war", ctx);
	}

	@Override
	public int stopAll(Context ctx) throws Exception {
		return runShellCmd("stop_all", ctx);
	}

	@Override
	public int upgradeKernel(Context ctx) throws Exception {
		return runShellCmd("upgrade_kernel", ctx);
	}

	@Override
	public int startContainer(Context ctx) throws Exception {
		return runShellCmd("start_container", ctx);
	}

	@Override
	public int checkContainerStatus(Context ctx) throws Exception {
		int exitCode = callQaService(ctx);
		return exitCode;
	}

	private int callQaService(Context ctx) throws IOException {
		String localIp = NetworkInterfaceManager.INSTANCE.getLocalHostAddress();
		DeployTask task = (DeployTask) ((KernelUpgradeContext) ctx).getTask();
		String qaServiceUrlPrefix = task.getQaServiceUrlPrefix();
		DomainHealthCheckInfo deployInfo = new DomainHealthCheckInfo(task.getDomain(), config.getEnv(), localIp,
				config.getContainerPort(), qaServiceUrlPrefix);

		int exitCode = Step.CODE_OK;
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
			exitCode = Step.CODE_OK;
			break;

		case TIMEOUT:
		case AGENT_LOCAL_EXCEPTION:
		case QA_LOCAL_EXCEPTION:
		case SUBMIT_FAILED:
		case FAIL:
			exitCode = Step.CODE_ERROR;
			break;

		default:
			logger.error(String.format("unexpected CheckResult type %s, treat as %s", checkResult, Step.CODE_ERROR));
			exitCode = Step.CODE_ERROR;
			break;

		}
		return exitCode;
	}

	@Override
	public int commit(Context ctx) throws Exception {
		return runShellCmd("commit", ctx);
	}

	@Override
	public int rollback(Context ctx) throws Exception {
		return runShellCmd("rollback", ctx);
	}
}

package com.dianping.phoenix.agent.core.task.processor.upgrade;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.PhoenixAgent;
import com.dianping.phoenix.agent.PhoenixAgentDryRun;
import com.dianping.phoenix.agent.core.task.workflow.Context;
import com.dianping.phoenix.agent.core.task.workflow.Step;
import com.dianping.phoenix.configure.ConfigManager;

public class DefaultAgentUpgradeStepProvider implements AgentUpgradeStepProvider {
	private static final Logger logger = Logger.getLogger(DefaultAgentUpgradeStepProvider.class);

	@Inject
	private ConfigManager config;

	private int runShellCmd(String shellFunc, Context ctx) throws Exception {
		AgentUpgradeContext myCtx = (AgentUpgradeContext) ctx;
		String script = jointShellCmd(shellFunc, ctx);
		int exitCode = myCtx.getScriptExecutor().exec(script, myCtx.getLogOut(), myCtx.getLogOut());
		return exitCode;
	}

	private String jointShellCmd(String shellFunc, Context ctx) {
		StringBuilder sb = new StringBuilder();
		AgentUpgradeContext myCtx = (AgentUpgradeContext) ctx;
		AgentUpgradeTask task = (AgentUpgradeTask) myCtx.getTask();

		logger.info(String.format("start upgrading agent to version %s", task.getAgentVersion()));

		String agentDocBase = String.format(config.getAgentDocBasePattern(), task.getAgentVersion());

		String agentGitUrl = task.getAgentGitUrl();
		String agentGitHost = null;
		try {
			agentGitHost = new URI(agentGitUrl).getHost();
		} catch (URISyntaxException e) {
			throw new RuntimeException(String.format("error parsing host from kernel git url %s", agentGitUrl), e);
		}

		sb.append(config.getAgentSelfUpgradeScriptFile().getAbsolutePath());
		sb.append(String.format(" --agent_git_url=\"%s\" ", task.getAgentGitUrl()));
		sb.append(String.format(" --agent_version=\"%s\" ", task.getAgentVersion()));
		sb.append(String.format(" --tx_log_file=\"%s\" ", myCtx.getUnderLyingFile()));
		sb.append(String.format(" --agent_doc_base=\"%s\" ", agentDocBase));
		sb.append(String.format(" --agent_git_host=\"%s\" ", agentGitHost));
		sb.append(String.format(" --func=\"%s\" ", shellFunc));
		sb.append(String.format(" --tmp_script_file=\"%s\" ", myCtx.getTempScriptFile()));
		sb.append(String.format(" --agent_class=\"%s\" ", PhoenixAgent.class.getName()));
		sb.append(String.format(" --agent_dryrun_class=\"%s\" ", PhoenixAgentDryRun.class.getName()));
		sb.append(String.format(" --dry_run_port=\"%d\" ", config.getDryrunPort()));
		sb.append(String.format(" --parent_pid=\"%s\" ", config.getPid()));

		return sb.toString();
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
	public int gitPull(Context ctx) throws Exception {
		return runShellCmd("gitpull", ctx);
	}

	@Override
	public int dryrunAgent(Context ctx) throws Exception {
		return runShellCmd("dryrun", ctx);
	}

	@Override
	public int upgradeAgent(Context ctx) throws Exception {
		return runShellCmd("upgrade", ctx);
	}
}

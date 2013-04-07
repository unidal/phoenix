package com.dianping.phoenix.agent.core.task.processor.upgrade;

import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.PhoenixAgent;
import com.dianping.phoenix.agent.PhoenixAgentDryRun;
import com.dianping.phoenix.agent.core.shell.ScriptExecutor;
import com.dianping.phoenix.agent.core.task.workflow.Step;
import com.dianping.phoenix.configure.ConfigManager;

public class DefaultAgentUpgradeStepProvider implements AgentUpgradeStepProvider {

	private static final Logger logger = Logger.getLogger(DefaultAgentUpgradeStepProvider.class);
	@Inject
	private ConfigManager config;
	@Inject
	ScriptExecutor scriptExecutor;

	private OutputStream logOut;
	private AgentUpgradeTask task;
	private String underLyingFile;
	private String tempScriptFile;

	private int runShellCmd(String shellFunc) throws Exception {
		String script = jointShellCmd(shellFunc);
		int exitCode = scriptExecutor.exec(script, logOut, logOut);
		return exitCode;
	}

	private String jointShellCmd(String shellFunc) {
		StringBuilder sb = new StringBuilder();

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
		sb.append(String.format(" -g \"%s\" ", task.getAgentGitUrl()));
		sb.append(String.format(" -v \"%s\" ", task.getAgentVersion()));
		sb.append(String.format(" -l \"%s\" ", underLyingFile));
		sb.append(String.format(" -a \"%s\" ", agentDocBase));
		sb.append(String.format(" -h \"%s\" ", agentGitHost));
		sb.append(String.format(" -f \"%s\" ", shellFunc));
		sb.append(String.format(" -t \"%s\" ", tempScriptFile));
		sb.append(String.format(" -c \"%s\" ", PhoenixAgent.class.getName()));
		sb.append(String.format(" -d \"%s\" ", PhoenixAgentDryRun.class.getName()));
		sb.append(String.format(" -p \"%d\" ", config.getDryrunPort()));
		sb.append(String.format(" -i \"%s\" ", config.getPid()));

		return sb.toString();
	}

	@Override
	public int prepare(AgentUpgradeTask task, OutputStream logOut, String underLyingFile) {
		this.task = task;
		this.logOut = logOut;
		this.underLyingFile = underLyingFile;
		this.tempScriptFile = "phoenix-agent-self-upgrade.sh." + System.currentTimeMillis();
		return Step.CODE_OK;
	}

	@Override
	public int init() throws Exception {
		return runShellCmd("init");
	}

	@Override
	public int checkArgument() throws Exception {
		return Step.CODE_OK;
	}

	@Override
	public int gitPull() throws Exception {
		return runShellCmd("gitpull");
	}

	@Override
	public int dryrunAgent() throws Exception {
		return runShellCmd("dryrun");
	}

	@Override
	public int upgradeAgent() throws Exception {
		return runShellCmd("upgrade");
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(new URI("ssh://git@192.168.7.108/arch/phoenix-agent.git").getHost());
	}
}

package com.dianping.phoenix.configure;

import java.io.File;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.entity.AgentConfig;
import com.dianping.phoenix.configure.entity.Config;
import com.dianping.phoenix.configure.entity.ConsoleConfig;
import com.dianping.phoenix.configure.entity.GitConfig;
import com.dianping.phoenix.configure.transform.DefaultSaxParser;

public class ConfigManager implements Initializable {
	@Inject
	private String m_configFile = "/data/appdatas/phoenix/config.xml";

	private Config m_config;

	private boolean m_showLogTimestamp = true; // for unit test purpose

	private void check() {
		if (m_config == null) {
			throw new RuntimeException("ConfigManager is not initialized properly!");
		}
	}

	public int getDeployConnectTimeout() {
		check();

		return m_config.getConsole().getDeployConnectTimeout();
	}
	
	public int getDeployGetlogRetrycount(){
		check();
		return m_config.getConsole().getDeployGetlogRetrycount();
	}
	
	public String getDeployLogUrl(String host, int deployId) {
		check();

		String pattern = m_config.getConsole().getDeployLogUrlPattern();

		return String.format(pattern, host, deployId);
	}

	public long getDeployRetryInterval() {
		check();

		int interval = m_config.getConsole().getDeployRetryInterval(); // in second

		return interval;
	}

	public String getDeployStatusUrl(String host, int deployId) {
		check();

		String pattern = m_config.getConsole().getDeployStatusUrlPattern();

		return String.format(pattern, host, deployId);
	}

	public String getDeployUrl(String type, String host, int deployId, String name, String version, boolean skipTest) {
		check();

		String gitUrl = String.format(m_config.getGit().getOriginUrl(), type);

		if (skipTest) {
			String pattern = m_config.getConsole().getDeployUrlSkipTestPattern();

			return String.format(pattern, host, deployId, name, version, type, gitUrl);
		} else {
			String pattern = m_config.getConsole().getDeployUrlPattern();
			String testServiceUrlPrefix = m_config.getConsole().getTestServiceUrlPrefix();
			long testServiceTimeout = m_config.getConsole().getTestServiceTimeout();

			return String.format(pattern, host, deployId, name, version, type, gitUrl, testServiceUrlPrefix,
			      testServiceTimeout);
		}
	}

	public String getGitOriginUrl(String type) {
		check();

		return String.format(m_config.getGit().getOriginUrl(), type);
	}

	public String getGitWorkingDir(String type) {
		check();

		return String.format(m_config.getGit().getLocalDir(), type);
	}

	public String getWarUrl(String type, String version) {
		check();

		return String.format(m_config.getWarUrlPattern(), type, version);
	}

	@Override
	public void initialize() throws InitializationException {
		try {
			File file = new File(m_configFile);

			if (file.isFile()) {
				String content = Files.forIO().readFrom(file, "utf-8");

				m_config = DefaultSaxParser.parse(content);

				if (m_config.getGit() == null) {
					m_config.setGit(new GitConfig());
				}

				if (m_config.getAgent() == null) {
					m_config.setAgent(new AgentConfig());
				}

				if (m_config.getConsole() == null) {
					m_config.setConsole(new ConsoleConfig());
				}
			} else {
				m_config = new Config();
				m_config.setGit(new GitConfig());
				m_config.setAgent(new AgentConfig());
				m_config.setConsole(new ConsoleConfig());
			}
		} catch (Exception e) {
			throw new InitializationException(String.format("Unable to load configuration file(%s)!", m_configFile), e);
		}
	}

	public boolean isShowLogTimestamp() {
		return m_showLogTimestamp;
	}

	public void setConfigFile(String configFile) {
		m_configFile = configFile;
	}

	public void setDeployRetryInterval(int retryInterval) {
		check();

		m_config.getConsole().setDeployRetryInterval(retryInterval); // in second
	}
}

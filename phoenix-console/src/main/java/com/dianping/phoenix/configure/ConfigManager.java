package com.dianping.phoenix.configure;

import java.io.File;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.entity.AgentConfig;
import com.dianping.phoenix.configure.entity.Config;
import com.dianping.phoenix.configure.entity.GitConfig;
import com.dianping.phoenix.configure.transform.DefaultSaxParser;

public class ConfigManager implements Initializable {
	@Inject
	private String m_configFile = "/data/appdatas/phoenix/config.xml";

	private Config m_config;

	private boolean m_showLogTimestamp = true; // for unit test purpose

	public String getDeployLogUrl(String host, int deployId) {
		if (m_config == null) {
			throw new RuntimeException("ConfigManager is not initialized properly!");
		} else {
			String pattern = m_config.getAgent().getDeployLogUrlPattern();

			return String.format(pattern, host, deployId);
		}
	}

	public long getDeployRetryInterval() {
		if (m_config == null) {
			throw new RuntimeException("ConfigManager is not initialized properly!");
		} else {
			int interval = m_config.getAgent().getDeployRetryInterval(); // in second

			return interval * 1000L;
		}
	}

	public String getDeployStatusUrl(String host, int deployId) {
		if (m_config == null) {
			throw new RuntimeException("ConfigManager is not initialized properly!");
		} else {
			String pattern = m_config.getAgent().getDeployStatusUrlPattern();

			return String.format(pattern, host, deployId);
		}
	}

	public String getDeployUrl(String host, int deployId, String name, String version) {
		if (m_config == null) {
			throw new RuntimeException("ConfigManager is not initialized properly!");
		} else {
			String pattern = m_config.getAgent().getDeployUrlPattern();

			return String.format(pattern, host, deployId, name, version);
		}
	}

	public String getGitOriginUrl() {
		if (m_config == null) {
			throw new RuntimeException("ConfigManager is not initialized properly!");
		} else {
			return m_config.getGit().getOriginUrl();
		}
	}

	public String getGitWorkingDir() {
		if (m_config == null) {
			throw new RuntimeException("ConfigManager is not initialized properly!");
		} else {
			return m_config.getGit().getLocalDir();
		}
	}

	public String getWarUrl(String version) {
		if (m_config == null) {
			throw new RuntimeException("ConfigManager is not initialized properly!");
		} else {
			return String.format(m_config.getWarUrlPattern(), version);
		}
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
			} else {
				m_config = new Config();
				m_config.setGit(new GitConfig());
				m_config.setAgent(new AgentConfig());
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
		if (m_config == null) {
			throw new RuntimeException("ConfigManager is not initialized properly!");
		} else {
			m_config.getAgent().setDeployRetryInterval(retryInterval); // in second
		}
	}
}

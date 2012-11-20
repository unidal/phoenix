package com.dianping.phoenix.configure;

import java.io.File;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.helper.Files;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.entity.Config;
import com.dianping.phoenix.configure.entity.GitConfig;
import com.dianping.phoenix.configure.transform.DefaultSaxParser;

public class ConfigManager implements Initializable {
	@Inject
	private String m_configFile = "/data/appdatas/phoenix/config.xml";

	private Config m_config;

	public String getGitOriginUrl() {
		if (m_config == null) {
			throw new RuntimeException(
					"ConfigManager is not initialized properly!");
		} else {
			return m_config.getGit().getOriginUrl();
		}
	}

	public String getGitPassword() {
		if (m_config == null) {
			throw new RuntimeException(
					"ConfigManager is not initialized properly!");
		} else {
			return m_config.getGit().getPassword();
		}
	}

	public String getGitUserName() {
		if (m_config == null) {
			throw new RuntimeException(
					"ConfigManager is not initialized properly!");
		} else {
			return m_config.getGit().getUserName();
		}
	}

	public String getGitWorkingDir() {
		if (m_config == null) {
			throw new RuntimeException(
					"ConfigManager is not initialized properly!");
		} else {
			return m_config.getGit().getLocalDir();
		}
	}

	public String getWarUrl(String version) {
		if (m_config == null) {
			throw new RuntimeException(
					"ConfigManager is not initialized properly!");
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
			} else {
				m_config = new Config();
				m_config.setGit(new GitConfig());
			}
		} catch (Exception e) {
			throw new InitializationException(String.format(
					"Unable to load configuration file(%s)!", m_configFile), e);
		}
	}

	public void setConfigFile(String configFile) {
		m_configFile = configFile;
	}
}

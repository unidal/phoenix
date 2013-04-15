package com.dianping.phoenix.configure;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
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

	private final static Logger logger = Logger.getLogger(ConfigManager.class);

	private final static String TOMCAT_LOADER_CLASS = "com.dianping.phoenix.bootstrap.Tomcat6WebappLoader";
	private final static String JBOSS_LOADER_CLASS = "com.dianping.phoenix.bootstrap.Jboss4WebappLoader";

	public enum ContainerType {
		TOMCAT, JBOSS
	}

	@Inject
	private String m_configFile = "/data/webapps/phoenix/phoenix-config/config.xml";

	private Config m_config;

	private ContainerType containerType;

	private File serverXml;

	private String loaderClass;

	private String m_pid;

	private void check() {
		if (m_config == null) {
			throw new RuntimeException("ConfigManager is not initialized properly!");
		}
	}

	/**
	 * Where the container installs
	 */
	public String getContainerInstallPath() {
		check();

		String containerInstallPath = m_config.getAgent().getContainerInstallPath().trim();

		// replace ~ to user home directory
		if (containerInstallPath.startsWith("~")) {
			containerInstallPath = System.getProperty("user.home") + containerInstallPath.substring(1);
		}

		return containerInstallPath;
	}

	/**
	 * Where kernel docBase locates. Use first %s to represent domain name and
	 * second %s to represent kernel version.
	 */
	public String getKernelDocBasePattern() {
		check();

		return m_config.getAgent().getKernelDocBasePattern();
	}

	public String getAgentDocBasePattern() {
		check();

		return m_config.getAgent().getAgentDocBasePattern();
	}

	/**
	 * Where domain docBase locates relative to the domain webapps root dir. Use
	 * %s to represent domain name. Make sure starts with "/".
	 */
	public String getDomainDocBaseFeaturePattern() {
		check();

		return m_config.getAgent().getDomainDocBaseKeywordPattern();
	}

	public ContainerType getContainerType() {
		check();

		return containerType;
	}

	public String getLoaderClass() {
		check();

		return loaderClass;
	}

	public File getServerXml() {
		check();

		return serverXml;
	}

	/**
	 * The interval when querying qa service task status, in milliseconds
	 */
	public int getQaServiceQueryInterval() {
		check();

		return m_config.getAgent().getTestServicePollInterval();
	}

	/**
	 * The HTTP port of container
	 */
	public int getContainerPort() {
		check();

		return m_config.getAgent().getContainerPort();
	}

	/**
	 * The env of agent
	 */
	public String getEnv() {
		check();

		return m_config.getEnv();
	}

	public int getDryrunPort() {
		check();

		return m_config.getAgent().getDryrunPort();
	}

	public String getPid() {
		return m_pid;
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

		// initialize containerType
		String containerInstallPath = getContainerInstallPath();
		File startupSh = new File(containerInstallPath + "/bin/startup.sh");
		File runSh = new File(containerInstallPath + "/bin/run.sh");
		if (startupSh.exists()) {
			containerType = ContainerType.TOMCAT;
		} else if (runSh.exists()) {
			containerType = ContainerType.JBOSS;
		} else {
			throw new InitializationException(String.format(
					"containerInstallPath %s does not have a valid tomcat or jboss installation", containerInstallPath));
		}

		// initialize pid
		m_pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

		// initialize loaderClass and serverXml
		if (containerType == ContainerType.TOMCAT) {
			loaderClass = TOMCAT_LOADER_CLASS;
			serverXml = new File(containerInstallPath + "/conf/server.xml");
		} else {
			loaderClass = JBOSS_LOADER_CLASS;
			serverXml = new File(String.format("%s/server/%s/deploy/jboss-web.deployer/server.xml",
					containerInstallPath, m_config.getAgent().getJbossServerName()));
		}

		logAllField(this);
		logAllField(m_config.getAgent());
		logAllField(m_config.getGit());

		makeShellScriptExecutable();

	}

	private void makeShellScriptExecutable() {
		File scriptDir = getAgentScriptFile().getParentFile();
		@SuppressWarnings("unchecked")
		Iterator<File> scriptIter = FileUtils.iterateFiles(scriptDir, new String[] { "sh" }, true);
		while (scriptIter != null && scriptIter.hasNext()) {
			scriptIter.next().setExecutable(true, false);
		}
	}

	private void logAllField(Object obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			try {
				logger.info(field.getName() + "=" + field.get(obj));
			} catch (Exception e) {
				logger.info(String.format("error log config field %s", field), e);
			}
		}
	}

	public void setConfigFile(String configFile) {
		m_configFile = configFile;
	}

	public int getUrlConnectTimeout() {
		return m_config.getAgent().getUrlConnectTimeout();
	}

	public int getUrlReadTimeout() {
		return m_config.getAgent().getUrlReadTimeout();
	}

	public File getAgentScriptFile() {
		File scriptFile = getScriptFile("agent.sh");
		return scriptFile;
	}

	public File getAgentStatusScriptFile() {
		File scriptFile = getScriptFile("agent_status.sh");
		return scriptFile;
	}

	public File getAgentSelfUpgradeScriptFile() {
		File scriptFile = getScriptFile("self_upgrade.sh");
		return scriptFile;
	}

	private File getScriptFile(String scriptFileName) {
		URL scriptUrl = this.getClass().getClassLoader().getResource("script/" + scriptFileName);
		if (scriptUrl == null) {
			throw new RuntimeException(scriptFileName + " not found");
		}
		return new File(scriptUrl.getPath());
	}

}

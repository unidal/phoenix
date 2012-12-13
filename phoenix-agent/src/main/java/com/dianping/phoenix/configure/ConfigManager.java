package com.dianping.phoenix.configure;

import java.io.File;
import java.lang.reflect.Field;

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
	private String m_configFile = "/data/appdatas/phoenix/config.xml";

	private Config m_config;
	
	private ContainerType containerType;
	
	private File serverXml;
	
	private String loaderClass;

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
		if(containerInstallPath.startsWith("~")) {
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
		
		// initialize loaderClass and serverXml
		if (containerType == ContainerType.TOMCAT) {
			loaderClass = TOMCAT_LOADER_CLASS;
			serverXml = new File(containerInstallPath + "/conf/server.xml");
		} else {
			loaderClass = JBOSS_LOADER_CLASS;
			serverXml = new File(containerInstallPath + "/server/default/deploy/jboss-web.deployer/server.xml");
		}
		
		logAllField(this);
		logAllField(m_config.getAgent());
		logAllField(m_config.getGit());
		
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

}

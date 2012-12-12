package com.dianping.phoenix.agent.core.task.processor.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Config {

	private final static Logger logger = Logger.getLogger(Config.class);
	private final static String TOMCAT_LOADER_CLASS = "com.dianping.phoenix.bootstrap.Tomcat6WebappLoader";
	private final static String JBOSS_LOADER_CLASS = "com.dianping.phoenix.bootstrap.Jboss4WebappLoader";
	private final static String CONFIG_FILE = "/data/appdatas/phoenix/agent.properties";

	/**
	 * Where the container installs
	 */
	private String containerInstallPath = System.getProperty("user.home") + "/Downloads/apache-tomcat-6.0.35/";

	/**
	 * Where kernel docBase locates. Use first %s to represent domain name and
	 * second %s to represent kernel version.
	 */
	private String kernelDocBasePattern = "/data/webapps/phoenix-kernel/%s";

	/**
	 * Where domain docBase locates relative to the domain webapps root dir. Use
	 * %s to represent domain name. Make sure starts with "/".
	 */
	private String domainDocBaseFeaturePattern = "/%s/current";

	/**
	 * The timeout when calling qa test service, in milliseconds
	 */
	private int qaServiceTimeout = 180 * 1000;

	/**
	 * The interval when querying qa service task status, in milliseconds
	 */
	// TODO
	private int qaServiceQueryInterval = 1000;

	/**
	 * The HTTP port of container
	 */
	private int containerPort = 8080;

	/**
	 * The env of agent
	 */
	private String env = "dev";

	private String kernelGitUrl = "ssh://git@10.1.4.81:58422/phoenix-kernel.git";

	private ContainerType containerType;

	public enum ContainerType {
		TOMCAT, JBOSS
	}

	private String loaderClass;

	private File serverXml;

	public Config() {

		File configFile = new File(CONFIG_FILE);
		Properties props = new Properties();
		if (configFile.exists()) {
			logger.info(String.format("fount config file %s", configFile.getAbsolutePath()));
			try {
				InputStream in = new FileInputStream(configFile);
				props.load(in);
			} catch (IOException e) {
				String msg = String.format("error reading config file %s", configFile.getAbsolutePath());
				logger.error(msg, e);
				throw new RuntimeException(msg, e);
			}
		}

		containerInstallPath = props.getProperty("containerInstallPath", containerInstallPath);
		kernelDocBasePattern = props.getProperty("kernelDocBasePattern", kernelDocBasePattern);
		domainDocBaseFeaturePattern = props.getProperty("domainDocBaseFeaturePattern", domainDocBaseFeaturePattern);
		qaServiceTimeout = Integer.parseInt(props.getProperty("qaServiceTimeout", Integer.toString(qaServiceTimeout)));
		qaServiceQueryInterval = Integer.parseInt(props.getProperty("qaServiceQueryInterval",
				Integer.toString(qaServiceQueryInterval)));
		containerPort = Integer.parseInt(props.getProperty("containerPort", Integer.toString(containerPort)));
		env = props.getProperty("env", env);
		kernelGitUrl = props.getProperty("kernelGitUrl", kernelGitUrl);

		File startupSh = new File(containerInstallPath + "/bin/startup.sh");
		File runSh = new File(containerInstallPath + "/bin/run.sh");
		if (startupSh.exists()) {
			containerType = ContainerType.TOMCAT;
		} else if (runSh.exists()) {
			containerType = ContainerType.JBOSS;
		} else {
			throw new RuntimeException(String.format(
					"containerInstallPath %s does not have a valid tomcat or jboss installation", containerInstallPath));
		}

		if (containerType == ContainerType.TOMCAT) {
			loaderClass = TOMCAT_LOADER_CLASS;
			serverXml = new File(containerInstallPath + "/conf/server.xml");
		} else {
			loaderClass = JBOSS_LOADER_CLASS;
			serverXml = new File(containerInstallPath + "/server/default/deploy/jboss-web.deployer/server.xml");
		}
		
		logConfig();
	}

	private void logConfig() {
		Field[] fields = this.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			try {
				logger.info(String.format("%s=%s", field.getName(), field.get(this)));
			} catch (Exception e) {
				logger.info(String.format("error log config field", field), e);
			}
		}
	}

	public String getContainerInstallPath() {
		return containerInstallPath;
	}

	public String getKernelDocBasePattern() {
		return kernelDocBasePattern;
	}

	public String getDomainDocBaseFeaturePattern() {
		return domainDocBaseFeaturePattern;
	}

	public ContainerType getContainerType() {
		return containerType;
	}

	public String getLoaderClass() {
		return loaderClass;
	}

	public File getServerXml() {
		return serverXml;
	}

	public int getQaServiceTimeout() {
		return qaServiceTimeout;
	}

	public int getQaServiceQueryInterval() {
		return qaServiceQueryInterval;
	}

	public int getContainerPort() {
		return containerPort;
	}

	public String getEnv() {
		return env;
	}

	public String getKernelGitUrl() {
		return kernelGitUrl;
	}
	
}

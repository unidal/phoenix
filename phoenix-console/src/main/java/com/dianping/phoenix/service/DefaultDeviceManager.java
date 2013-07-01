package com.dianping.phoenix.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.agent.response.entity.Domain;
import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.device.entity.Attribute;
import com.dianping.phoenix.device.entity.Device;
import com.dianping.phoenix.device.entity.Responce;
import com.dianping.phoenix.device.transform.DefaultSaxParser;
import com.dianping.phoenix.project.entity.BussinessLine;
import com.dianping.phoenix.project.entity.Host;
import com.dianping.phoenix.project.entity.Project;
import com.dianping.phoenix.project.entity.Root;
import com.dianping.phoenix.service.netty.AgentStatusFetcher;

public class DefaultDeviceManager implements DeviceManager, Initializable, LogEnabled {

	@Inject
	private ConfigManager m_configManager;

	@Inject
	private AgentStatusFetcher m_agentStatusFetcher;

	private Logger m_logger;

	private static final String KEY_OWNER = "rd_duty";

	private static final String KEY_IP = "private_ip";

	private static final String KEY_STATUS = "status";

	private static final String KEY_ENV = "env";

	private AtomicReference<List<BussinessLine>> m_bizLinesInfo = new AtomicReference<List<BussinessLine>>();

	private AtomicReference<List<BussinessLine>> m_bizLines = new AtomicReference<List<BussinessLine>>();

	private Project findProjectBy(String name) throws Exception {
		String ipUrlPattern = m_configManager.getCmdbIpUrlPattern();
		Responce root = readCmdb(String.format(ipUrlPattern, name));
		Project project = new Project(name);
		project.setDescription("");
		if (root != null && root.getDevices() != null) {
			for (Device device : root.getDevices()) {
				Map<String, Attribute> attributeMap = device.getAttributes();
				if (attributeMap == null) {
					continue;
				}
				Attribute owner = attributeMap.get(KEY_OWNER);
				Attribute ip = attributeMap.get(KEY_IP);
				Attribute env = attributeMap.get(KEY_ENV);
				Attribute status = attributeMap.get(KEY_STATUS);
				if (owner != null && owner.getText() != null && owner.getText().length() > 0) {
					project.addOwner(owner.getText());
				}
				Host host = new Host();
				if (ip != null) {
					project.addHost(host);
					host.setIp(ip.getText());
					if (env != null) {
						host.setEnv(env.getText());
					}
					if (status != null) {
						host.setStatus(status.getText());
					}
				}
			}
			if (project.getHosts() != null) {
				Collections.sort(project.getHosts(), new IPComparator());
			}

		}
		if (project.getHosts() != null) {
			m_logger.info("Fetching Status of Agent.");
			m_agentStatusFetcher.fetchPhoenixAgentStatus(project.getHosts());
			analysisProject(project);
			m_logger.info("Status fetching finished.");
		}
		return project;
	}

	private void analysisProject(Project project) {
		for (Host host : project.getHosts()) {
			if ("ok".equals(host.getAgentStatus())) {
				project.setActiveCount(project.getActiveCount() + 1);
			} else {
				project.setInactiveCount(project.getInactiveCount() + 1);
			}

			for (Domain domain : host.getDomains()) {
				if (domain.getWar() != null) {
					String v = domain.getWar().getVersion();
					project.getAppVersions().add(v == null || v.length() == 0 ? "Unknow" : v);
				}
				if (domain.getKernel() != null && domain.getKernel().getWar() != null) {
					String v = domain.getKernel().getWar().getVersion();
					project.getKernelVersions().add(v == null || v.length() == 0 ? "Unknow" : v);
				}
			}
		}
	}
	protected static class IPComparator implements Comparator<Host> {

		@Override
		public int compare(Host o1, Host o2) {
			String ip1 = o1.getIp();
			String ip2 = o2.getIp();
			return getValueOfIP(ip1).compareTo(getValueOfIP(ip2));
		}

		private Long getValueOfIP(String ip) {
			Long result = 0L;
			for (String num : ip.split("\\.")) {
				try {
					result = (result << 8) + Long.parseLong(num);
				} catch (NumberFormatException e) {
					result = result << 8;
				}
			}
			return result;
		}
	}

	@Override
	public void initialize() throws InitializationException {
		m_logger.info("Initializing ConfigFileWatchDog thread.");
		new ConfigFileWatchdog().setDelay(30).start();// check changes 1/10MINs
		m_logger.info("ConfigFileWatchDog thread started.");
		m_logger.info("Initializing AgentStatusWatchdog thread.");
		new AgentStatusWatchdog().setDelay(5).start();// refresh status 1/5MINs
		m_logger.info("AgentStatusWatchdog thread started.");
	}

	private Responce readCmdb(String url) {
		try {
			URL cmdbUrl = new URL(url);
			InputStream in = null;
			in = cmdbUrl.openStream();
			return DefaultSaxParser.parse(in);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<BussinessLine> getBussinessLineList() {
		return m_bizLines.get();
	}

	private class ConfigFileWatchdog extends Thread {

		public static final long DEFAULT_DELAY = 1;

		private long m_delay = DEFAULT_DELAY;

		private long m_lastRefresh = 0;

		public ConfigFileWatchdog() {
			setDaemon(true);
			checkAndConfigure();
		}

		public ConfigFileWatchdog setDelay(int minutes) {
			m_delay = minutes;
			return this;
		}

		private void checkAndConfigure() {
			String path1 = "/data/appdatas/phoenix/project.xml";
			String path2 = "/com/dianping/phoenix/deploy/project.xml";

			File f = new File(path1);
			if (!f.exists() || !f.isFile()) {
				f = new File(getClass().getResource(path2).getFile());
			}
			if (!f.exists() || !f.isFile()) {
				throw new RuntimeException(String.format("Can't find project.xml at [%s] nor classpath.", path1));
			}

			if (f.lastModified() > m_lastRefresh) {
				m_lastRefresh = System.currentTimeMillis();

				Root r = null;
				try {
					r = com.dianping.phoenix.project.transform.DefaultSaxParser.parse(new FileInputStream(f));
				} catch (Exception e) {
					throw new RuntimeException("Can't parse project.xml.", e);
				}

				List<BussinessLine> l = new ArrayList<BussinessLine>();
				l.addAll(r.getBussinessLines().values());

				m_bizLinesInfo.set(l);
			}
		}

		@Override
		public void run() {
			while (true) {
				try {
					TimeUnit.MINUTES.sleep(m_delay);
					checkAndConfigure();
				} catch (Exception e) {
					m_logger.warn("Refresh config failed.", e);
				}
			}
		}
	}

	private class AgentStatusWatchdog extends Thread {
		public static final long DEFAULT_DELAY = 5;

		private long m_delay = DEFAULT_DELAY;

		public AgentStatusWatchdog setDelay(int minutes) {
			m_delay = minutes;
			return this;
		}

		public AgentStatusWatchdog() {
			setDaemon(true);
			refreshAgentStatus(false);
		}

		private void refreshAgentStatus(boolean needInteval) {
			List<BussinessLine> bizInfos = m_bizLinesInfo.get();

			List<BussinessLine> newBizLines = new ArrayList<BussinessLine>();
			for (BussinessLine bizInfo : bizInfos) {
				BussinessLine bizLine = new BussinessLine(bizInfo.getName());
				for (Entry<String, Project> entry : bizInfo.getProjects().entrySet()) {
					String name = entry.getKey();
					try {
						m_logger.info("Finding project: " + name);
						Project project = findProjectBy(name);
						m_logger.info("Project: " + name + " finished");
						bizLine.addProject(project);
						if (needInteval) {
							TimeUnit.SECONDS.sleep(m_delay * 60 / bizInfos.size() / bizInfo.getProjects().size());
						}
					} catch (InterruptedException e) {
						// ignore it
					} catch (Exception e) {
						m_logger.warn(String.format("Can not fetch agents status of project [%s]", name), e);
						e.printStackTrace();
					}
				}
				newBizLines.add(bizLine);
			}
			m_bizLines.set(newBizLines);
		}

		@Override
		public void run() {
			try {
				TimeUnit.MINUTES.sleep(m_delay);
			} catch (InterruptedException e) {
				// ignore it
			}
			while (true) {
				try {
					refreshAgentStatus(true);
				} catch (Exception e) {
					m_logger.warn("Refresh agent status failed.", e);
				}
			}
		}
	}

	@Override
	public void enableLogging(Logger logger) {
		m_logger = logger;
	}

	@Override
	public Project getProjectByName(String name) {
		List<BussinessLine> bizs = m_bizLines.get();
		for (BussinessLine biz : bizs) {
			Map<String, Project> projects = biz.getProjects();
			if (projects.containsKey(name)) {
				return projects.get(name);
			}
		}
		return new Project(name);
	}

	@Override
	public Project refreshProjectMannully(String name) {
		Project p = new Project(name);

		try {
			p = findProjectBy(name);
		} catch (Exception e) {
			throw new RuntimeException("Refresh project status failed.", e);
		}

		List<BussinessLine> bizs = m_bizLines.get();
		for (BussinessLine biz : bizs) {
			if (biz.getProjects().containsKey(name)) {
				biz.getProjects().put(name, p);
				break;
			}
		}
		return p;
	}
}

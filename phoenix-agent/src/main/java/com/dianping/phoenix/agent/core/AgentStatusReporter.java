package com.dianping.phoenix.agent.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.phoenix.agent.response.entity.Domain;
import com.dianping.phoenix.agent.response.entity.Lib;
import com.dianping.phoenix.agent.response.entity.Response;
import com.dianping.phoenix.agent.response.entity.War;
import com.dianping.phoenix.configure.ConfigManager;

public class AgentStatusReporter extends ContainerHolder implements Initializable {
	Logger logger = Logger.getLogger(AgentStatusReporter.class);

	@Inject
	private ContainerManager m_containerManager;
	@Inject
	private ConfigManager m_configManager;

	private class AgentHeartbeat implements Runnable {
		@Override
		public void run() {
			Response resp;
			try {
				resp = m_containerManager.reportContainerStatus();

				// cat agent infos
				Cat.getProducer().logEvent(
						"AGENT",
						toValidVersion(resp.getVersion()) + ":" + resp.getIp(),
						Event.SUCCESS,
						"ContainerType=" + resp.getContainer().getName() + "\nContainerInstallPath="
								+ resp.getContainer().getInstallPath() + "\nContainerVersion="
								+ toValidVersion(resp.getContainer().getVersion()) + "\nContainerStatus="
								+ resp.getContainer().getStatus());
				for (Domain domain : resp.getDomains()) {
					War kernelWar = domain.getKernel().getWar();

					// cat kernel infos
					for (Lib lib : kernelWar.getLibs()) {
						Cat.getProducer().logEvent(
								kernelWar.getName() + ":" + toValidVersion(kernelWar.getVersion()),
								lib.getGroupId() == null ? "UnknowGroupId" : lib.getGroupId() + ":"
										+ lib.getArtifactId() + ":" + toValidVersion(lib.getVersion()), Event.SUCCESS,
								null);
					}

					// cat war infos
					StringBuilder domainLibsKVPair = new StringBuilder();
					boolean firstLib = true;
					for (Lib lib : domain.getWar().getLibs()) {
						domainLibsKVPair.append(firstLib ? "" : "\n");
						domainLibsKVPair.append(//
								String.format("%s:%s:%s",
										lib.getGroupId() == null ? "UnknowGroupId" : lib.getGroupId(),
										lib.getArtifactId(), toValidVersion(lib.getVersion())));
						firstLib = firstLib ? false : firstLib;
					}
					Cat.getProducer().logEvent(
							domain.getWar().getName() + ":" + toValidVersion(domain.getWar().getVersion()),
							toValidVersion(kernelWar.getVersion()) + ":" + resp.getIp(), Event.SUCCESS,
							domainLibsKVPair.toString());
				}
			} catch (Exception e1) {
				logger.error("Phoenix Agent heartbeat failed.");
			}
		}
	}

	private static String toValidVersion(String version) {
		String unknowVersion = "UnknowVersion";
		if (version != null) {
			String versionStr = version.trim().toLowerCase();
			if (versionStr.length() <= 0 || "null".equals(versionStr) || "n/a".equals(versionStr)) {
				return unknowVersion;
			}
			return versionStr;
		}
		return unknowVersion;
	}

	@Override
	public void initialize() {
		Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {

			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setDaemon(true);
				t.setName("AgentHeartbeatToCat-" + m_configManager.getAgentHeartbeatFreq());
				return t;
			}
		}).scheduleWithFixedDelay(new AgentHeartbeat(), 0, m_configManager.getAgentHeartbeatFreq(), TimeUnit.MINUTES);
	}
}

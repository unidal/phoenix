package com.dianping.phoenix.agent.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.phoenix.agent.response.entity.Domain;
import com.dianping.phoenix.agent.response.entity.Lib;
import com.dianping.phoenix.agent.response.entity.Response;
import com.dianping.phoenix.agent.response.entity.War;
import com.dianping.phoenix.configure.ConfigManager;

public class AgentStatusReporter extends ContainerHolder {
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
				Cat.getProducer().logEvent(
						resp.getIp() + "::AGENT::" + resp.getVersion(),
						resp.getContainer().getName() + "::" + resp.getContainer().getStatus(),
						Message.SUCCESS,
						"InstallPath=" + resp.getContainer().getInstallPath() + "&Version="
								+ resp.getContainer().getVersion());
				for (Domain domain : resp.getDomains()) {
					String catType = resp.getIp() + "::DOMAIN::" + domain.getWar().getName() + "::"
							+ domain.getWar().getVersion();
					for (Lib lib : domain.getWar().getLibs()) {
						String catName = "LIB::" + lib.getArtifactId() + "::" + lib.getVersion();
						Cat.getProducer().logEvent(catType, catName, Message.SUCCESS, lib.getGroupId());
					}

					War kernelWar = domain.getKernel().getWar();
					String catKernelType = resp.getIp() + "::KERNEL::" + domain.getWar().getName() + "::"
							+ kernelWar.getVersion();
					for (Lib lib : kernelWar.getLibs()) {
						String catKernelName = "KLIB::" + lib.getArtifactId() + "::" + lib.getVersion();
						Cat.getProducer().logEvent(catKernelType, catKernelName, Message.SUCCESS, lib.getGroupId());
					}
				}
			} catch (Exception e1) {
				logger.error("Phoenix Agent heartbeat failed.");
			}
		}
	}

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

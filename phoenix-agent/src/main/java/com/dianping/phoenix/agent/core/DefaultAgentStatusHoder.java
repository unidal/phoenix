package com.dianping.phoenix.agent.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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

public class DefaultAgentStatusHoder extends ContainerHolder implements Initializable, AgentStatusHolder {
	Logger logger = Logger.getLogger(DefaultAgentStatusHoder.class);

	@Inject
	private ContainerManager m_containerManager;
	@Inject
	private ConfigManager m_configManager;

	private static AtomicReference<Response> m_agentStatus = new AtomicReference<Response>(new Response());

	private class AgentHeartbeat implements Runnable {
		@Override
		public void run() {
			Response resp;
			try {
				resp = m_containerManager.reportContainerStatus();
				m_agentStatus.set(resp);
				if (resp != null) {
					// cat agent infos
					Cat.getProducer().logEvent(
							"AGENT",
							toValidVersion(resp.getVersion()) + ":" + resp.getIp(),
							Event.SUCCESS,
							"ContainerType=" + resp.getContainer().getName() + "\nContainerInstallPath="
									+ resp.getContainer().getInstallPath() + "\nContainerVersion="
									+ toValidVersion(resp.getContainer().getVersion()) + "\nContainerStatus="
									+ resp.getContainer().getStatus());
					if (resp.getDomains() != null && resp.getDomains().size() > 0) {
						for (Domain domain : resp.getDomains()) {
							War kernelWar = null;
							if (domain.getKernel() != null) {
								kernelWar = domain.getKernel().getWar();
								if (kernelWar != null) {
									// cat kernel infos
									for (Lib lib : kernelWar.getLibs()) {
										Cat.getProducer().logEvent(
												kernelWar.getName() + ":" + toValidVersion(kernelWar.getVersion()),
												lib.getGroupId() == null ? "UnknowGroupId" : lib.getGroupId() + ":"
														+ lib.getArtifactId() + ":" + toValidVersion(lib.getVersion()),
												Event.SUCCESS, null);
									}
								}
							}

							// cat war infos
							StringBuilder domainLibsKVPair = new StringBuilder();
							boolean firstLib = true;
							if (domain.getWar() != null) {
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
										toValidVersion(kernelWar == null ? null : kernelWar.getVersion()) + ":"
												+ resp.getIp(), Event.SUCCESS, domainLibsKVPair.toString());
							}
						}
					} else {
						logger.warn("No domains found on this server.");
					}
				} else {
					throw new RuntimeException("Can not parse agent infos.");
				}
			} catch (Exception e) {
				logger.error("Phoenix Agent heartbeat failed.", e);
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

	@Override
	public Response getAgentStatusResponse() {
		Response resp = m_agentStatus.get();
		if (resp != null) {
			return resp;
		} else {
			onAgentStatusChanged();
			return m_agentStatus.get();
		}
	}

	@Override
	public void onAgentStatusChanged() {
		try {
			Response resp = m_containerManager.reportContainerStatus();
			m_agentStatus.set(resp);
		} catch (Exception e) {
			logger.error("Can not get agent status.", e);
		}
	}
}

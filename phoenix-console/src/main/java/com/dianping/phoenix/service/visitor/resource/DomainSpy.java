package com.dianping.phoenix.service.visitor.resource;

import static com.dianping.phoenix.utils.StringUtils.getDefaultValueIfBlank;
import static com.dianping.phoenix.utils.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.dianping.phoenix.agent.resource.entity.App;
import com.dianping.phoenix.agent.resource.entity.Domain;
import com.dianping.phoenix.agent.resource.entity.Host;
import com.dianping.phoenix.agent.resource.entity.Kernel;
import com.dianping.phoenix.agent.resource.entity.Lib;
import com.dianping.phoenix.agent.resource.entity.PhoenixAgent;

public class DomainSpy extends BaseResourceVisitor {
	private static final String NONE = "-";
	private static final String UNKNOWN = "Unknown";

	private Map<AppKey, Map<String, VersionBox>> m_appLibInfo = new HashMap<AppKey, Map<String, VersionBox>>();
	private Map<AppKey, MetaInfo> m_appMetaInfo = new HashMap<AppKey, MetaInfo>();

	private Host m_currentHost;
	private String m_currentAgentVersion;
	private String m_currentAgentStatus;
	private App m_currentApp;
	private AppKey m_currentKey;

	private boolean m_visitKernel;

	private List<List<String>> m_result;

	public DomainSpy(Domain domain, List<String> domainJarNameList) {
		visitDomain(domain);
		m_result = generateDomainLibInfos(m_appMetaInfo, m_appLibInfo, domainJarNameList);
	}

	@Override
	public void visitHost(Host host) {
		m_currentHost = host;

		PhoenixAgent agent = host.getPhoenixAgent();
		m_currentAgentVersion = agent == null || isBlank(agent.getVersion()) ? NONE : agent.getVersion();
		m_currentAgentStatus = agent == null || isBlank(agent.getStatus()) ? NONE : agent.getStatus();

		super.visitHost(host);
	}

	@Override
	public void visitApp(App app) {
		m_currentApp = app;

		m_currentKey = new AppKey();
		m_currentKey.setAppName(getDefaultValueIfBlank(m_currentApp.getName(), UNKNOWN));
		m_currentKey.setHostIp(getDefaultValueIfBlank(m_currentHost.getIp(), UNKNOWN));

		Kernel kernel = app.getKernel();
		MetaInfo meta = new MetaInfo();
		meta.setAgentStatus(m_currentAgentStatus);
		meta.setAgentVersion(m_currentAgentVersion);
		meta.setKernelVersion(kernel == null || isBlank(kernel.getVersion()) ? NONE : kernel.getVersion());
		m_appMetaInfo.put(m_currentKey, meta);

		Map<String, VersionBox> map = new HashMap<String, VersionBox>();
		m_appLibInfo.put(m_currentKey, map);

		m_visitKernel = false;

		super.visitApp(app);
	}

	@Override
	public void visitKernel(Kernel kernel) {
		m_visitKernel = true;

		super.visitKernel(kernel);
	}

	@Override
	public void visitLib(Lib lib) {
		Map<String, VersionBox> libToVersions = m_appLibInfo.get(m_currentKey);
		if (libToVersions.containsKey(lib.getArtifactId())) {
			if (m_visitKernel) {
				libToVersions.get(lib.getArtifactId()).setKernelVersion(lib.getVersion());
			} else {
				libToVersions.get(lib.getArtifactId()).setLibVersion(lib.getVersion());
			}
		} else {
			VersionBox box = new VersionBox();
			if (m_visitKernel) {
				box.setKernelVersion(lib.getVersion());
			} else {
				box.setLibVersion(lib.getVersion());
			}
			libToVersions.put(lib.getArtifactId(), box);
		}
	}

	private class AppKey {
		private String m_hostIp;
		private String m_appName;

		public String getHostIp() {
			return m_hostIp;
		}
		public String getAppName() {
			return m_appName;
		}
		public void setHostIp(String hostIp) {
			m_hostIp = hostIp;
		}
		public void setAppName(String appName) {
			m_appName = appName;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((m_appName == null) ? 0 : m_appName.hashCode());
			result = prime * result + ((m_hostIp == null) ? 0 : m_hostIp.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AppKey other = (AppKey) obj;
			if (m_appName == null) {
				if (other.m_appName != null)
					return false;
			} else if (!m_appName.equals(other.m_appName))
				return false;
			if (m_hostIp == null) {
				if (other.m_hostIp != null)
					return false;
			} else if (!m_hostIp.equals(other.m_hostIp))
				return false;
			return true;
		}
	}

	private class VersionBox {
		private String m_kernelVersion;
		private String m_libVersion;

		public String getKernelVersion() {
			return m_kernelVersion;
		}
		public String getLibVersion() {
			return m_libVersion;
		}
		public void setKernelVersion(String kernelVersion) {
			m_kernelVersion = kernelVersion;
		}
		public void setLibVersion(String libVersion) {
			m_libVersion = libVersion;
		}
	}

	private class MetaInfo {
		private String m_agentVersion;
		private String m_agentStatus;
		private String m_kernelVersion;

		public String getAgentVersion() {
			return m_agentVersion;
		}
		public String getAgentStatus() {
			return m_agentStatus;
		}
		public String getKernelVersion() {
			return m_kernelVersion;
		}
		public void setAgentVersion(String agentVersion) {
			m_agentVersion = agentVersion;
		}
		public void setAgentStatus(String agentStatus) {
			m_agentStatus = agentStatus;
		}
		public void setKernelVersion(String kernelVersion) {
			m_kernelVersion = kernelVersion;
		}
	}

	private List<List<String>> generateDomainLibInfos(Map<AppKey, MetaInfo> appMetaInfo,
			Map<AppKey, Map<String, VersionBox>> applibInfo, List<String> libSequence) {
		List<List<String>> list = new ArrayList<List<String>>();
		List<String> head = new ArrayList<String>();
		head.add("HostIP");
		head.add("APPName");
		head.add("AgentVersion");
		head.add("AgentStatus");
		head.add("KernelVersion");
		for (String item : libSequence) {
			head.add(item);
			head.add("#" + item);
		}
		list.add(head);

		for (Entry<AppKey, Map<String, VersionBox>> entry : applibInfo.entrySet()) {
			List<String> rowData = new ArrayList<String>();

			String ip = entry.getKey().getHostIp();
			String app = entry.getKey().getAppName();

			MetaInfo meta = appMetaInfo.get(entry.getKey());
			Map<String, VersionBox> libVersions = entry.getValue();

			rowData.add(ip);
			rowData.add(app);
			rowData.add(meta.getAgentVersion());
			rowData.add(meta.getAgentStatus());
			rowData.add(meta.getKernelVersion());

			for (String item : libSequence) {
				if (libVersions.containsKey(item)) {
					rowData.add(getDefaultValueIfBlank(libVersions.get(item).getLibVersion(), NONE));
					rowData.add(getDefaultValueIfBlank(libVersions.get(item).getKernelVersion(), NONE));
				} else {
					rowData.add(NONE);
					rowData.add(NONE);
				}
			}

			list.add(rowData);
		}
		return list;
	}

	public List<List<String>> getDomainInfoAsTable() {
		return m_result;
	}
}

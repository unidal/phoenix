package com.dianping.phoenix.service.visitor.resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.dianping.phoenix.agent.resource.entity.App;
import com.dianping.phoenix.agent.resource.entity.Domain;
import com.dianping.phoenix.agent.resource.entity.Host;
import com.dianping.phoenix.agent.resource.entity.Kernel;
import com.dianping.phoenix.agent.resource.entity.Lib;
import com.dianping.phoenix.agent.resource.entity.PhoenixAgent;
import com.dianping.phoenix.agent.resource.entity.Product;
import com.dianping.phoenix.agent.resource.entity.Resource;
import com.dianping.phoenix.utils.StringUtils;

public class ResourceAnalyzer extends BaseResourceVisitor {
	private static final String m_statusOK = "ok";

	private Set<String> m_agentVersionSet = new HashSet<String>();
	private Set<String> m_jarNameSet = new HashSet<String>();
	private Map<String, Set<String>> m_domainToJarNameSet = new HashMap<String, Set<String>>();
	private Map<String, Domain> m_domains = new HashMap<String, Domain>();

	private Domain m_currentDomain;
	private Set<String> m_currentDomainJarNameSet;
	private int m_currentDomainActiveCount;
	private int m_currentDomainInactiveCount;
	private int m_currentProductActiveCount;
	private int m_currentProductInactiveCount;

	public ResourceAnalyzer(Resource resource) {
		visitResource(resource);
	}

	public Set<String> getAgentVersionSet() {
		return m_agentVersionSet;
	}

	public Set<String> getJarNameSet() {
		return m_jarNameSet;
	}

	public Map<String, Set<String>> getDomainToJarNameSet() {
		return m_domainToJarNameSet;
	}

	public Map<String, Domain> getDomains() {
		return m_domains;
	}

	@Override
	public void visitDomain(Domain domain) {
		m_currentDomain = domain;
		m_currentDomainActiveCount = 0;
		m_currentDomainInactiveCount = 0;

		if (!StringUtils.isBlank(domain.getName())) {
			m_domains.put(domain.getName(), domain);

			m_currentDomainJarNameSet = new HashSet<String>();
			super.visitDomain(domain);
			m_domainToJarNameSet.put(domain.getName(), m_currentDomainJarNameSet);
			m_currentDomain.setActiveCount(m_currentDomainActiveCount);
			m_currentDomain.setInactiveCount(m_currentDomainInactiveCount);

			m_currentProductActiveCount += m_currentDomainActiveCount;
			m_currentProductInactiveCount += m_currentDomainInactiveCount;
		}
	}

	@Override
	public void visitPhoenixAgent(PhoenixAgent phoenixAgent) {
		if (m_statusOK.equals(phoenixAgent.getStatus())) {
			m_currentDomainActiveCount++;
		} else {
			m_currentDomainInactiveCount++;
		}
		if (!StringUtils.isBlank(phoenixAgent.getVersion())) {
			m_agentVersionSet.add(phoenixAgent.getVersion());
		}
	}

	@Override
	public void visitLib(Lib lib) {
		if (!StringUtils.isBlank(lib.getArtifactId())) {
			m_jarNameSet.add(lib.getArtifactId());
			m_currentDomainJarNameSet.add(lib.getArtifactId());
		}
	}

	@Override
	public void visitHost(Host host) {
		String owner = StringUtils.getDefaultValueIfBlank(host.getOwner(), "NONE");
		if (!"NONE".equals(owner)) {
			m_currentDomain.addOwner(owner);
		}
		if (host.getPhoenixAgent() == null) {
			m_currentDomainInactiveCount++;
		}
		super.visitHost(host);
	}

	@Override
	public void visitApp(App app) {
		m_currentDomain.addAppVersion(StringUtils.getDefaultValueIfBlank(app.getVersion(), "NONE"));
		super.visitApp(app);
	}

	@Override
	public void visitKernel(Kernel kernel) {
		m_currentDomain.addKernelVersion(StringUtils.getDefaultValueIfBlank(kernel.getVersion(), "NONE"));
		super.visitKernel(kernel);
	}

	@Override
	public void visitProduct(Product product) {
		m_currentProductActiveCount = 0;
		m_currentProductInactiveCount = 0;

		super.visitProduct(product);

		product.setProductActiveCount(m_currentProductActiveCount);
		product.setProductInactiveCount(m_currentProductInactiveCount);
	}

}

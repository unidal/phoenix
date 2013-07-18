package com.dianping.phoenix.service.visitor.resource;

import java.util.HashSet;
import java.util.Set;

import com.dianping.phoenix.agent.resource.entity.Host;
import com.dianping.phoenix.agent.resource.entity.Resource;
import com.dianping.phoenix.console.page.home.Payload;
import com.dianping.phoenix.service.VersionComparator;

public class AgentFilterStrategy extends BaseResourceVisitor implements FilterStrategy {
	private static final VersionComparator m_versionComparator = new VersionComparator();

	private Resource m_resource;

	private Set<String> m_strategy;

	private String m_operator;
	private String m_version;

	public AgentFilterStrategy(Resource resource, Payload payload) {
		m_resource = resource;
		m_operator = payload.getAgentOperator();
		m_version = payload.getAgentVersion();

		if (m_operator != null && m_operator.trim().length() > 0 && m_version != null && m_version.trim().length() > 0) {
			m_strategy = new HashSet<String>();
			super.visitResource(m_resource);
		}
	}

	private boolean isMatchedRule(String operator, String left, String right) {
		int res = m_versionComparator.compare(left, right);
		if ((operator.equals("<") && res < 0) || (operator.equals("=") && res == 0)
				|| (operator.equals(">") && res > 0)) {
			return true;
		}
		return false;
	}

	@Override
	public Set<String> getStrategy() {
		return m_strategy;
	}

	@Override
	public Resource getResource() {
		return m_resource;
	}

	@Override
	public void visitHost(Host host) {
		if (host.getPhoenixAgent() != null) {
			if (isMatchedRule(m_operator, host.getPhoenixAgent().getVersion(), m_version)) {
				m_strategy.add(host.getIp());
			}
		}
	}
}

package com.dianping.phoenix.service.visitor.resource;

import com.dianping.phoenix.agent.resource.entity.Domain;
import com.dianping.phoenix.agent.resource.entity.Product;
import com.dianping.phoenix.agent.resource.entity.Resource;

public class FilteredResourceBuilder extends BaseResourceVisitor {
	private FilterStrategy m_strategy;
	private Resource m_resource;
	private Product m_product;
	private Domain m_domain;

	public FilteredResourceBuilder(FilterStrategy strategy) {
		m_strategy = strategy;
		if (m_strategy.getResource() != null) {
			m_strategy.getResource().accept(this);
		}
	}

	@Override
	public void visitDomain(Domain domain) {
		m_domain = new Domain();
		m_domain.setName(domain.getName());
		for (String ip : m_strategy.getStrategy()) {
			if (domain.getHosts().containsKey(ip)) {
				m_domain.addHost(domain.getHosts().get(ip));
			}
		}
		if (m_domain.getHosts().size() > 0) {
			m_product.addDomain(m_domain);
		}
	}

	@Override
	public void visitProduct(Product product) {
		m_product = new Product();
		m_product.setName(product.getName());

		super.visitProduct(product);

		if (m_product.getDomains().size() > 0) {
			m_resource.addProduct(m_product);
		}
	}

	@Override
	public void visitResource(Resource resource) {
		if (m_strategy.getStrategy() == null || m_strategy.getStrategy().size() == 0) {
			m_resource = m_strategy.getResource();
			return;
		}
		m_resource = new Resource();
		super.visitResource(resource);
		
		new ResourceAnalyzer(m_resource).analysis();
	}

	public Resource getFilteredResource() {
		return m_resource;
	}
}

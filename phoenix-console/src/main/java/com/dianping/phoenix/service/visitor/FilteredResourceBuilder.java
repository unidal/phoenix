package com.dianping.phoenix.service.visitor;

import com.dianping.phoenix.agent.resource.IVisitor;
import com.dianping.phoenix.agent.resource.entity.App;
import com.dianping.phoenix.agent.resource.entity.Container;
import com.dianping.phoenix.agent.resource.entity.Domain;
import com.dianping.phoenix.agent.resource.entity.Host;
import com.dianping.phoenix.agent.resource.entity.Kernel;
import com.dianping.phoenix.agent.resource.entity.Lib;
import com.dianping.phoenix.agent.resource.entity.PhoenixAgent;
import com.dianping.phoenix.agent.resource.entity.Product;
import com.dianping.phoenix.agent.resource.entity.Resource;

public class FilteredResourceBuilder implements IVisitor {
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
	public void visitApp(App app) {
		// ignore it
	}

	@Override
	public void visitContainer(Container container) {
		// ignore it
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
	public void visitHost(Host host) {
		// ignore it
	}

	@Override
	public void visitKernel(Kernel kernel) {
		// ignore it
	}

	@Override
	public void visitLib(Lib lib) {
		// ignore it
	}

	@Override
	public void visitPhoenixAgent(PhoenixAgent phoenixAgent) {
		// ignore it
	}

	@Override
	public void visitProduct(Product product) {
		m_product = new Product();
		m_product.setName(product.getName());
		for (Domain domain : product.getDomains().values()) {
			domain.accept(this);
		}
		if (m_product.getDomains().size() > 0) {
			m_resource.addProduct(m_product);
		}
	}

	@Override
	public void visitResource(Resource resource) {
		if (m_strategy.getStrategy().size() == 0 && m_strategy.isEmptyFilter()) {
			m_resource = m_strategy.getResource();
			return;
		}
		m_resource = new Resource();
		for (Product product : resource.getProducts().values()) {
			product.accept(this);
		}
	}

	public Resource getFilteredResource() {
		return m_resource;
	}
}

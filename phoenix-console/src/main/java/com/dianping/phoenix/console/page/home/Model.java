package com.dianping.phoenix.console.page.home;

import java.util.List;

import org.unidal.web.mvc.ViewModel;

import com.dianping.phoenix.agent.resource.entity.Domain;
import com.dianping.phoenix.agent.resource.entity.Product;
import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.dal.deploy.Deliverable;
import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.deploy.DeployPolicy;

public class Model extends ViewModel<ConsolePage, Action, Context> {
	private List<Product> m_products;

	private Domain m_domain;

	private List<Deliverable> m_deliverables;

	private DeployPolicy[] m_policies;

	private Deployment m_activeDeployment;

	private List<String> m_libs;

	public Model(Context ctx) {
		super(ctx);
	}

	public Deployment getActiveDeployment() {
		return m_activeDeployment;
	}

	@Override
	public Action getDefaultAction() {
		return Action.HOME;
	}

	public List<Deliverable> getDeliverables() {
		return m_deliverables;
	}

	public DeployPolicy[] getPolicies() {
		return m_policies;
	}

	public void setActiveDeployment(Deployment activeDeployment) {
		m_activeDeployment = activeDeployment;
	}

	public void setDeliverables(List<Deliverable> deliverables) {
		m_deliverables = deliverables;
	}

	public void setPolicies(DeployPolicy[] policies) {
		m_policies = policies;
	}

	public List<Product> getProducts() {
		return m_products;
	}

	public void setProducts(List<Product> products) {
		m_products = products;
	}

	public Domain getDomain() {
		return m_domain;
	}

	public void setDomain(Domain domain) {
		m_domain = domain;
	}

	public void setLibs(List<String> libs) {
		m_libs = libs;
	}

	public List<String> getLibs() {
		return m_libs;
	}
}

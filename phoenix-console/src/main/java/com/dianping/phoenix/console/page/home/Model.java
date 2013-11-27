package com.dianping.phoenix.console.page.home;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.unidal.web.mvc.ViewModel;
import org.unidal.webres.json.JsonSerializer;

import com.dianping.phoenix.agent.resource.entity.Domain;
import com.dianping.phoenix.agent.resource.entity.Product;
import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.dal.deploy.Deliverable;
import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.deploy.DeployPolicy;
import com.dianping.phoenix.service.visitor.resource.DomainSpy;

public class Model extends ViewModel<ConsolePage, Action, Context> {
	private List<Product> m_products;

	private Domain m_domain;

	private List<Deliverable> m_deliverables;

	private DeployPolicy[] m_policies;

	private Deployment m_activeDeployment;

	private List<String> m_libs;

	private List<String> m_agentVersions;

	private List<List<String>> m_domainInfos;

	private String m_domainInfoJson;

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
		Collections.sort(m_products, new Comparator<Product>() {
			@Override
			public int compare(Product o1, Product o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
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

	public List<String> getAgentVersions() {
		return m_agentVersions;
	}

	public void setAgentVersions(List<String> agentVersions) {
		this.m_agentVersions = agentVersions;
	}

	public List<List<String>> getDomainInfos() {
		return m_domainInfos;
	}

	public void setDomainInfos(Domain domain, List<String> domainJarNameList) {
		DomainSpy spy = new DomainSpy(domain, domainJarNameList);
		m_domainInfos = spy.getDomainInfoAsTable();
		try {
			setDomainInfoJson(JsonSerializer.getInstance().serialize(m_domainInfos));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getDomainInfoJson() {
		return m_domainInfoJson;
	}

	public void setDomainInfoJson(String domainInfoJson) {
		m_domainInfoJson = domainInfoJson;
	}
}

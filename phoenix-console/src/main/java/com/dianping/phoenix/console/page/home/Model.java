package com.dianping.phoenix.console.page.home;

import java.util.List;

import org.unidal.web.mvc.ViewModel;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.console.dal.deploy.Deliverable;
import com.dianping.phoenix.deploy.DeployPolicy;
import com.dianping.phoenix.project.entity.BussinessLine;
import com.dianping.phoenix.project.entity.Project;

public class Model extends ViewModel<ConsolePage, Action, Context> {
	private List<BussinessLine> m_bussinessLines;

	private Project m_project;

	private List<Deliverable> m_deliverables;

	private DeployPolicy[] m_policies;

	private Deployment m_activeDeployment;

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

	public Project getProject() {
		return m_project;
	}
	
	public List<BussinessLine> getBussinessLines() {
		return m_bussinessLines;
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

	public void setProject(Project project) {
		m_project = project;
	}

	public void setBussinessLines(List<BussinessLine> bussinessLines) {
		this.m_bussinessLines = bussinessLines;
	}

}

package com.dianping.phoenix.console.page.home;

import java.util.List;

import org.unidal.web.mvc.ViewModel;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.dal.deploy.Version;
import com.dianping.phoenix.deploy.DeployPolicy;
import com.dianping.phoenix.deploy.entity.Project;

public class Model extends ViewModel<ConsolePage, Action, Context> {
	private List<Project> m_projects;

	private Project m_project;

	private List<String> m_deployPlans;

	private List<Version> m_versions;

	private DeployPolicy[] m_policies;

	public Model(Context ctx) {
		super(ctx);
	}

	@Override
	public Action getDefaultAction() {
		return Action.HOME;
	}

	public List<String> getDeployPlans() {
		return m_deployPlans;
	}

	public DeployPolicy[] getPolicies() {
   	return m_policies;
   }

	public Project getProject() {
		return m_project;
	}

	public List<Project> getProjects() {
		return m_projects;
	}

	public List<Version> getVersions() {
		return m_versions;
	}

	public void setDeployPlans(List<String> deployPlans) {
		m_deployPlans = deployPlans;
	}

	public void setPolicies(DeployPolicy[] policies) {
		m_policies = policies;
	}

	public void setProject(Project project) {
		m_project = project;
	}

	public void setProjects(List<Project> projects) {
		m_projects = projects;
	}

	public void setVersions(List<Version> versions) {
		m_versions = versions;
	}
}

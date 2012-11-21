package com.dianping.phoenix.console.page.home;

import java.util.List;

import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;
import org.unidal.web.mvc.payload.annotation.ObjectMeta;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.service.DeploymentPlan;
import com.dianping.phoenix.service.DeploymentPolicy;

public class Payload implements ActionPayload<ConsolePage, Action> {
	private ConsolePage m_page;

	@FieldMeta("op")
	private Action m_action;

	@FieldMeta("domain")
	private String m_domain;

	@FieldMeta("keyword")
	private String m_keyword;

	@FieldMeta("project")
	private String m_project;

	@FieldMeta("host")
	private List<String> m_hosts;

	@ObjectMeta("plan")
	private DeploymentPlan m_plan;

	@FieldMeta("deploy")
	private boolean m_deploy;

	@Override
	public Action getAction() {
		return m_action;
	}

	public String getDomain() {
		return m_domain;
	}

	public List<String> getHosts() {
		return m_hosts;
	}

	public String getKeyword() {
		return m_keyword;
	}

	@Override
	public ConsolePage getPage() {
		return m_page;
	}

	public DeploymentPlan getPlan() {
		return m_plan;
	}

	public boolean isDeploy() {
		return m_deploy;
	}

	public String getProject() {
		return m_project;
	}

	public void setAction(String action) {
		m_action = Action.getByName(action, Action.HOME);
	}

	// public void setDeployPlan(DeploymentPlan plan) {
	// m_plan = plan;
	// }
	//
	// public void setDomain(String domain) {
	// m_domain = domain;
	// }
	//
	// public void setHosts(List<String> hosts) {
	// m_hosts = hosts;
	// }
	//
	// public void setKeyword(String keyword) {
	// m_keyword = keyword;
	// }

	@Override
	public void setPage(String page) {
		m_page = ConsolePage.getByName(page, ConsolePage.HOME);
	}
//
//	public void setProject(String project) {
//		m_project = project;
//	}

	@Override
	public void validate(ActionContext<?> ctx) {
		if (m_action == null) {
			m_action = Action.HOME;
		}

		if (m_action == Action.PROJECT) {
			if (m_plan == null) {
				m_plan = new DeploymentPlan();
			}

			if (m_plan.getPolicy() == null) {
				m_plan.setPolicy(String.valueOf(DeploymentPolicy.ONE_BY_ONE.getId()));
			}
		} else if (m_action == Action.DEPLOY) {
			if (m_hosts == null || m_hosts.isEmpty()) {
				ctx.addError("project.hosts", null);
			}

			if (m_plan == null) {
				m_plan = new DeploymentPlan();
			}

			if (m_plan.getVersion() == null) {
				ctx.addError("project.version", null);
			}
		}
	}
}

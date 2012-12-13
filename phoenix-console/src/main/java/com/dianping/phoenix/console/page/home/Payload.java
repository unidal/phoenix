package com.dianping.phoenix.console.page.home;

import java.util.List;

import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;
import org.unidal.web.mvc.payload.annotation.ObjectMeta;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.deploy.DeployPolicy;

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
	
	@FieldMeta("deploy")
	private boolean m_deploy;
	
	@FieldMeta("watch")
	private boolean m_watch;

	@ObjectMeta("plan")
	private DeployPlan m_plan;

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

	public DeployPlan getPlan() {
		return m_plan;
	}

	public String getProject() {
		return m_project;
	}

	public boolean isDeploy() {
		return m_deploy;
	}

	public boolean isWatch() {
		return m_watch;
	}

	public void setAction(String action) {
		m_action = Action.getByName(action, Action.HOME);
	}

	public void setDeploy(boolean deploy) {
		m_deploy = deploy;
	}

	@Override
	public void setPage(String page) {
		m_page = ConsolePage.getByName(page, ConsolePage.HOME);
	}

	public void setWatch(boolean watch) {
		m_watch = watch;
	}

	@Override
	public void validate(ActionContext<?> ctx) {
		if (m_action == null) {
			m_action = Action.HOME;
		}

		if (m_action == Action.PROJECT) {
			if (m_plan == null) {
				m_plan = new DeployPlan();
			}

			if (m_plan.getPolicy() == null) {
				m_plan.setPolicy(DeployPolicy.ONE_BY_ONE.getId());
			}
		} else if (m_action == Action.DEPLOY) {
			if (m_hosts == null || m_hosts.isEmpty()) {
				ctx.addError("project.hosts", null);
			}

			if (m_plan == null) {
				m_plan = new DeployPlan();
			}

			if (m_plan.getVersion() == null) {
				ctx.addError("project.version", null);
			}
		}
	}
}

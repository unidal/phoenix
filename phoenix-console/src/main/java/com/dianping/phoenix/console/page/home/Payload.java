package com.dianping.phoenix.console.page.home;

import java.util.List;

import com.dianping.phoenix.console.ConsolePage;
import com.site.web.mvc.ActionContext;
import com.site.web.mvc.ActionPayload;
import com.site.web.mvc.payload.annotation.FieldMeta;

public class Payload implements ActionPayload<ConsolePage, Action> {
	private ConsolePage m_page;

	@FieldMeta("op")
	private Action m_action;

	@FieldMeta("name")
	private String m_projectName;

	@FieldMeta("keyword")
	private String m_keyword;

	@FieldMeta("hosts")
	private List<String> m_hosts;

	@FieldMeta("plan")
	private String m_plan;

	@Override
	public Action getAction() {
		return m_action;
	}

	public String getPlan() {
		return m_plan;
	}

	public List<String> getHosts() {
		return m_hosts;
	}

	public String getKeyword() {
		return this.m_keyword;
	}

	@Override
	public ConsolePage getPage() {
		return m_page;
	}

	public String getProjectName() {
		return m_projectName;
	}

	public void setAction(String action) {
		m_action = Action.getByName(action, Action.HOME);
	}

	public void setDeployPlan(String deployPlan) {
		this.m_plan = deployPlan;
	}

	public void setHosts(List<String> hosts) {
		this.m_hosts = hosts;
	}

	public void setKeyword(String keyword) {
		this.m_keyword = keyword;
	}

	@Override
	public void setPage(String page) {
		m_page = ConsolePage.getByName(page, ConsolePage.HOME);
	}

	public void setProjectName(String projectName) {
		this.m_projectName = projectName;
	}

	@Override
	public void validate(ActionContext<?> ctx) {
		if (m_action == null) {
			m_action = Action.HOME;
		}
	}
}

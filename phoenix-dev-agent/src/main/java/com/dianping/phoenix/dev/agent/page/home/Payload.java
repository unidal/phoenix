package com.dianping.phoenix.dev.agent.page.home;

import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;

import com.dianping.phoenix.dev.agent.AgentPage;

public class Payload implements ActionPayload<AgentPage, Action> {
	private AgentPage m_page;

	@FieldMeta("op")
	private Action m_action;
	@FieldMeta("projectName")
	private String m_projectName;

	public void setAction(Action action) {
		m_action = action;
	}

	@Override
	public Action getAction() {
		return m_action;
	}

	@Override
	public AgentPage getPage() {
		return m_page;
	}

	@Override
	public void setPage(String page) {
		m_page = AgentPage.getByName(page, AgentPage.HOME);
	}

	@Override
	public void validate(ActionContext<?> ctx) {
	}

	public String getProjectName() {
		return m_projectName;
	}

}

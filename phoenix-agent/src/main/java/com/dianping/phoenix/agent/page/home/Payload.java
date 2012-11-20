package com.dianping.phoenix.agent.page.home;

import com.dianping.phoenix.agent.AgentPage;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;

public class Payload implements ActionPayload<AgentPage, Action> {
	private AgentPage m_page;

	@FieldMeta("op")
	private Action m_action;

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
}

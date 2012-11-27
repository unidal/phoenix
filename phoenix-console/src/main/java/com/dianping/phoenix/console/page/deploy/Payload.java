package com.dianping.phoenix.console.page.deploy;

import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;

import com.dianping.phoenix.console.ConsolePage;

public class Payload implements ActionPayload<ConsolePage, Action> {
	private ConsolePage m_page;

	@FieldMeta("op")
	private Action m_action = Action.VIEW;

	@FieldMeta("plan")
	private String m_plan;
	
	@FieldMeta("offset")
	private int m_offset;

	@Override
	public Action getAction() {
		return m_action;
	}

	public int getOffset() {
   	return m_offset;
   }

	@Override
	public ConsolePage getPage() {
		return m_page;
	}

	public String getPlan() {
		return m_plan;
	}

	public void setAction(String action) {
		m_action = Action.getByName(action, Action.VIEW);
	}

	public void setOffset(int offset) {
   	m_offset = offset;
   }

	@Override
	public void setPage(String page) {
		m_page = ConsolePage.getByName(page, ConsolePage.DEPLOY);
	}

	public void setPlan(String plan) {
		m_plan = plan;
	}

	@Override
	public void validate(ActionContext<?> ctx) {
	}
}

package com.dianping.service.editor.page.home;

import java.util.Map;

import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;
import org.unidal.web.mvc.payload.annotation.ObjectMeta;

import com.dianping.service.editor.EditorPage;

public class Payload implements ActionPayload<EditorPage, Action> {
	private EditorPage m_page;

	@FieldMeta("op")
	private Action m_action;

	@FieldMeta("serviceType")
	private String m_serviceType;

	@FieldMeta("alias")
	private String m_alias;

	@ObjectMeta("properties")
	private Map<String, String> m_properties;

	@Override
	public Action getAction() {
		return m_action;
	}

	public String getAlias() {
		return m_alias;
	}

	@Override
	public EditorPage getPage() {
		return m_page;
	}

	public Map<String, String> getProperties() {
		return m_properties;
	}

	public String getServiceType() {
		return m_serviceType;
	}

	public void setAction(String action) {
		m_action = Action.getByName(action, Action.VIEW);
	}

	@Override
	public void setPage(String page) {
		m_page = EditorPage.getByName(page, EditorPage.HOME);
	}

	@Override
	public void validate(ActionContext<?> ctx) {
		if (m_action == null) {
			m_action = Action.VIEW;
		}
	}
}

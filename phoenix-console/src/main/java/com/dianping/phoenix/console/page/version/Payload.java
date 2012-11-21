package com.dianping.phoenix.console.page.version;

import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;

import com.dianping.phoenix.console.ConsolePage;

public class Payload implements ActionPayload<ConsolePage, Action> {
	private ConsolePage m_page;

	@FieldMeta("op")
	private Action m_action;

	@FieldMeta("version")
	private String m_version;

	@FieldMeta("desc")
	private String m_description;
	
	@FieldMeta("id")
	private int m_id;

	@Override
	public Action getAction() {
		return m_action;
	}

	public String getDescription() {
		return m_description;
	}

	public int getId() {
   	return m_id;
   }

	@Override
	public ConsolePage getPage() {
		return m_page;
	}

	public String getVersion() {
		return m_version;
	}

	public void setAction(String action) {
		m_action = Action.getByName(action, Action.VIEW);
	}

	public void setDescription(String description) {
		m_description = description;
	}

	public void setId(int id) {
   	m_id = id;
   }

	@Override
	public void setPage(String page) {
		m_page = ConsolePage.getByName(page, ConsolePage.VERSION);
	}

	public void setVersion(String version) {
		m_version = version;
	}

	@Override
	public void validate(ActionContext<?> ctx) {
		if (m_action == null) {
			m_action = Action.VIEW;
		}
	}
}

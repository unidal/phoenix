package com.dianping.phoenix.console.page.version;

import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;

import com.dianping.phoenix.console.ConsolePage;

public class Payload implements ActionPayload<ConsolePage, Action> {
	private ConsolePage m_page;

	@FieldMeta("op")
	private Action m_action;

	@FieldMeta("type")
	private String m_type;

	@FieldMeta("version")
	private String m_version;

	@FieldMeta("desc")
	private String m_description;

	@FieldMeta("id")
	private int m_id;

	@FieldMeta("index")
	private int m_index;

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

	public int getIndex() {
		return m_index;
	}

	@Override
	public ConsolePage getPage() {
		return m_page;
	}

	public String getVersion() {
		return m_version;
	}

	public String getType() {
		return m_type;
	}

	public void setAction(String action) {
		m_action = Action.getByName(action, Action.VIEW);
	}

	public void setIndex(int index) {
		m_index = index;
	}

	@Override
	public void setPage(String page) {
		m_page = ConsolePage.getByName(page, ConsolePage.VERSION);
	}

	@Override
	public void validate(ActionContext<?> ctx) {
		if (m_action == null) {
			m_action = Action.VIEW;
		}

		if (m_type == null || m_type.length() == 0) {
			m_type = "phoenix-kernel";
		} else {
			m_type = m_type.trim();
		}

		if (m_version != null) {
			m_version = m_version.trim();
		}
	}
}

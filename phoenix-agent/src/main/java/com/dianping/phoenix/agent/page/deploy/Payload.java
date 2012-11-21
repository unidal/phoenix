package com.dianping.phoenix.agent.page.deploy;

import com.dianping.phoenix.agent.AgentPage;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;

public class Payload implements ActionPayload<AgentPage, Action> {
	private AgentPage m_page;

	@FieldMeta("op")
	private Action m_action;

	@FieldMeta("deployId")
	private long m_deployId;

	@FieldMeta("version")
	private String m_version;

	@FieldMeta("domain")
	private String m_domain;

	@FieldMeta("offset")
	private int m_offset;

	@Override
	public Action getAction() {
		return m_action == null ? Action.DEFAULT : m_action;
	}

	@Override
	public AgentPage getPage() {
		return m_page;
	}

	public String getDomain() {
		return m_domain;
	}

	public long getDeployId() {
		return m_deployId;
	}

	public String getVersion() {
		return m_version;
	}

	public int getOffset() {
		return m_offset;
	}
	
	public void setAction(String action) {
		m_action = Action.getByName(action, Action.DEFAULT);
	}

	@Override
	public void setPage(String page) {
		m_page = AgentPage.getByName(page, AgentPage.DEPLOY);
	}

	public void setVersion(String version) {
		m_version = version;
	}

	@Override
	public void validate(ActionContext<?> ctx) {
	}
}

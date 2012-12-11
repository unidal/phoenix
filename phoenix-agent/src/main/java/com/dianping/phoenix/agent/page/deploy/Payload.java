package com.dianping.phoenix.agent.page.deploy;

import com.dianping.phoenix.agent.AgentPage;

import org.unidal.lookup.util.StringUtils;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.ErrorObject;
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

	@FieldMeta("br")
	private int m_br;

	@FieldMeta("qaServiceUrlPrefix")
	private String m_qaServiceUrlPrefix;

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

	public int getBr() {
		return m_br;
	}

	public String getQaServiceUrlPrefix() {
		return m_qaServiceUrlPrefix;
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
		StringBuilder sb = new StringBuilder();
		switch (m_action) {
		case DEPLOY:
			checkCommonArguments(ctx, sb);
			if (StringUtils.isEmpty(StringUtils.trimAll(m_domain))) {
				sb.append("domain can not be null,");
			}
			if (StringUtils.isEmpty(StringUtils.trimAll(m_version))) {
				sb.append("version can not be null,");
			}
			break;

		case DETACH:
			checkCommonArguments(ctx, sb);
			if (StringUtils.isEmpty(StringUtils.trimAll(m_domain))) {
				sb.append("domain can not be empty");
			}
			break;

		case STATUS:
		case CANCEL:
		case GETLOG:
			checkCommonArguments(ctx, sb);
			break;

		}

		if (sb.length() > 0) {
			ctx.addError(new ErrorObject(sb.toString()));
		}
	}

	private void checkCommonArguments(ActionContext<?> ctx, StringBuilder sb) {
		if (!validDeployId(m_deployId)) {
			sb.append("deployId invalid");
		}
	}

	private boolean validDeployId(long deployId) {
		return deployId > 0;
	}
}

package com.dianping.phoenix.agent.page.deploy;

import com.dianping.phoenix.agent.AgentPage;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.unidal.lookup.util.StringUtils;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.ErrorObject;
import org.unidal.web.mvc.payload.annotation.FieldMeta;

public class Payload implements ActionPayload<AgentPage, Action> {
	private AgentPage m_page;

	@FieldMeta("op")
	private Action m_action = Action.DEFAULT;

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

	@FieldMeta("qaServiceTimeout")
	private int m_qaServiceTimeout;

	@FieldMeta("kernelGitUrl")
	private String m_kernelGitUrl;

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

	public int getQaServiceTimeout() {
		return m_qaServiceTimeout;
	}

	public String getKernelGitUrl() {
		return m_kernelGitUrl;
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
		switch (m_action) {
		case DEPLOY:
			checkCommonArguments(ctx);
			if (StringUtils.isEmpty(StringUtils.trimAll(m_domain))) {
				ctx.addError(new ErrorObject("domain.missing,"));
			}

			if (StringUtils.isEmpty(StringUtils.trimAll(m_version))) {
				ctx.addError(new ErrorObject("version.missing"));
			}

			if (StringUtils.isEmpty(StringUtils.trimAll(m_kernelGitUrl))) {
				ctx.addError(new ErrorObject("kernelGitUrl.missing"));
			}

			break;

		case DETACH:
			checkCommonArguments(ctx);
			if (StringUtils.isEmpty(StringUtils.trimAll(m_domain))) {
				ctx.addError(new ErrorObject("domain.invalid"));
			}
			break;

		case STATUS:
		case CANCEL:
		case GETLOG:
			checkCommonArguments(ctx);
			if (m_offset < 0) {
				ctx.addError(new ErrorObject("offset.invalid"));
			}
			break;

		}

	}

	private void checkCommonArguments(ActionContext<?> ctx) {
		if (!validDeployId(m_deployId)) {
			ctx.addError(new ErrorObject("deployId.invalid"));
		}
	}

	private boolean validDeployId(long deployId) {
		return deployId > 0;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
	
}

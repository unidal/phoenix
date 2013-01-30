package com.dianping.phoenix.agent.page.deploy;

import com.dianping.phoenix.agent.AgentPage;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.ErrorObject;
import org.unidal.web.mvc.payload.annotation.FieldMeta;

public class Payload implements ActionPayload<AgentPage, Action> {

	public enum WarType {
		Kernel("phoenix-kernel"), Agent("phoenix-agent"), Unknown("unknown");
		private String m_name;

		private WarType(String name) {
			m_name = name;
		}

		public static WarType get(String name) {
			for (WarType warType : WarType.values()) {
				if (warType.m_name.equals(name)) {
					return warType;
				}
			}
			return Unknown;
		}

	}

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

	@FieldMeta("gitUrl")
	private String m_gitUrl;

	@FieldMeta("warType")
	private String m_warType;

	@Override
	public Action getAction() {
		return m_action == null ? Action.DEFAULT : m_action;
	}

	@Override
	public AgentPage getPage() {
		return m_page;
	}

	public String getGitUrl() {
		return m_gitUrl;
	}

	public WarType getWarType() {
		return WarType.get(m_warType);
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
			if (StringUtils.isBlank(m_gitUrl)) {
				ctx.addError(new ErrorObject("gitUrl.missing"));
			}

			if (StringUtils.isBlank(m_version)) {
				ctx.addError(new ErrorObject("version.missing"));
			}

			WarType warType = WarType.get(m_warType);
			switch (warType) {

			case Kernel:
				if (StringUtils.isBlank(m_domain)) {
					ctx.addError(new ErrorObject("domain.missing,"));
				}
				break;

			case Agent:
				break;

			default:
				ctx.addError(new ErrorObject("warType.invalid"));
				break;
			}
			break;

		case DETACH:
			checkCommonArguments(ctx);
			if (StringUtils.isBlank(m_domain)) {
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

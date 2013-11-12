package com.dianping.phoenix.console.page.home;

import java.util.List;

import org.unidal.web.mvc.ActionContext;
import org.unidal.web.mvc.ActionPayload;
import org.unidal.web.mvc.payload.annotation.FieldMeta;
import org.unidal.web.mvc.payload.annotation.ObjectMeta;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.deploy.DeployPolicy;
import com.dianping.phoenix.deploy.DeployType;

public class Payload implements ActionPayload<ConsolePage, Action> {
	private ConsolePage m_page;

	@FieldMeta("op")
	private Action m_action;

	@FieldMeta("type")
	private String m_type;

	@FieldMeta("domain")
	private String m_domain;

	@FieldMeta("keyword")
	private String m_keyword;

	@FieldMeta("project")
	private String m_project;

	@FieldMeta("host")
	private List<String> m_hosts;

	@FieldMeta("deploy")
	private boolean m_deploy;

	@FieldMeta("watch")
	private boolean m_watch;

	@ObjectMeta("plan")
	private DeployPlan m_plan;

	@FieldMeta("dependency")
	private List<String> m_dependencies;

	@FieldMeta("operator")
	private List<String> m_operators;

	@FieldMeta("version")
	private List<String> m_versions;

	@FieldMeta("joint")
	private List<String> m_joints;

	@FieldMeta("agentversion")
	private String m_agentVersion;

	@FieldMeta("agentoperator")
	private String m_agentOperator;

	@FieldMeta("getdomain")
	private String m_domaininfo;

	@Override
	public Action getAction() {
		return m_action;
	}

	public String getDomain() {
		return m_domain;
	}

	public List<String> getHosts() {
		return m_hosts;
	}

	public String getKeyword() {
		return m_keyword;
	}

	@Override
	public ConsolePage getPage() {
		return m_page;
	}

	public DeployPlan getPlan() {
		return m_plan;
	}

	public String getProject() {
		return m_project;
	}

	public boolean isDeploy() {
		return m_deploy;
	}

	public boolean isWatch() {
		return m_watch;
	}

	public void setAction(String action) {
		m_action = Action.getByName(action, Action.HOME);
	}

	public void setDeploy(boolean deploy) {
		m_deploy = deploy;
	}

	@Override
	public void setPage(String page) {
		m_page = ConsolePage.getByName(page, ConsolePage.HOME);
	}

	public void setWatch(boolean watch) {
		m_watch = watch;
	}

	@Override
	public void validate(ActionContext<?> ctx) {
		boolean isActionNull = false;
		if (m_action == null) {
			isActionNull = true;
			m_action = Action.HOME;
		}

		if (m_plan == null) {
			m_plan = new DeployPlan();
		}

		if (m_type != null) {
			DeployType type = DeployType.get(m_type);
			m_plan.setWarType(type);
		}

		if (m_action == Action.PROJECT) {
			if (m_plan.getPolicy() == null) {
				m_plan.setPolicy(DeployPolicy.ONE_BY_ONE.getId());
			}

			if ("phoenix-agent".equals(m_type)) {
				m_plan.setSkipTest(true);
			}
		} else if (m_action == Action.DEPLOY) {
			if (m_hosts == null || m_hosts.isEmpty()) {
				ctx.addError("project.hosts", null);
			}

			if (m_plan.getVersion() == null) {
				ctx.addError("project.version", null);
			}
		}

		if (m_action == Action.HOME) {
			if (!isActionNull) {
				if ("phoenix-kernel".equals(m_type)) {
					if (isListEmpty(m_dependencies) || isListEmpty(m_versions) || isListEmpty(m_operators)
							|| m_dependencies.size() != m_versions.size()
							|| m_dependencies.size() != m_operators.size()
							|| (m_joints != null && m_dependencies.size() != m_joints.size() + 1)) {
						ctx.addError("search.error", null);
					}
				} else if ("phoenix-agent".equals(m_type)) {
					if (m_agentOperator == null || m_agentOperator.trim().length() == 0 || m_agentVersion == null
							|| m_agentVersion.trim().length() == 0) {
						ctx.addError("search.error", null);
					}
				}
			}
		}
	}

	private boolean isListEmpty(List<String> list) {
		if (list != null) {
			for (String str : list) {
				if (str.trim().length() != 0) {
					return false;
				}
			}
		}
		return true;
	}
	public List<String> getDependencies() {
		return m_dependencies;
	}

	public List<String> getOperators() {
		return m_operators;
	}

	public List<String> getVersions() {
		return m_versions;
	}

	public List<String> getJoints() {
		return m_joints;
	}

	public void setDependencies(List<String> dependencies) {
		this.m_dependencies = dependencies;
	}

	public void setOperators(List<String> operators) {
		this.m_operators = operators;
	}

	public void setVersions(List<String> versions) {
		this.m_versions = versions;
	}

	public void setJoints(List<String> joints) {
		this.m_joints = joints;
	}

	public String getAgentVersion() {
		return m_agentVersion;
	}

	public String getAgentOperator() {
		return m_agentOperator;
	}

	public void setAgentVersion(String agentVersion) {
		this.m_agentVersion = agentVersion;
	}

	public void setAgentOperator(String agentOperator) {
		this.m_agentOperator = agentOperator;
	}

	public String getType() {
		return m_type;
	}

	public String getDomaininfo() {
		return m_domaininfo;
	}

	public void setDomaininfo(String domaininfo) {
		m_domaininfo = domaininfo;
	}
}

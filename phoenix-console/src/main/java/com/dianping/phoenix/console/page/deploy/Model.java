package com.dianping.phoenix.console.page.deploy;

import org.unidal.web.mvc.ViewModel;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.deploy.model.entity.DeployModel;

public class Model extends ViewModel<ConsolePage, Action, Context> {
	private DeployPlan m_plan;

	private DeployModel m_deploy;

	public Model(Context ctx) {
		super(ctx);
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}

	public DeployModel getDeploy() {
		return m_deploy;
	}

	public DeployPlan getPlan() {
		return m_plan;
	}

	public void setDeploy(DeployModel deploy) {
		m_deploy = deploy;
	}

	public void setPlan(DeployPlan plan) {
		m_plan = plan;
	}
}

package com.dianping.phoenix.console.page.deploy;

import org.unidal.web.mvc.ViewModel;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.deploy.DeployPolicy;
import com.dianping.phoenix.deploy.model.entity.DeployModel;

public class Model extends ViewModel<ConsolePage, Action, Context> {
	private DeployModel m_deploy;

	private DeployPolicy[] m_policies;

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

	public DeployPolicy[] getPolicies() {
   	return m_policies;
   }

	public void setDeploy(DeployModel deploy) {
		m_deploy = deploy;
	}

	public void setPolicies(DeployPolicy[] policies) {
   	m_policies = policies;
   }
}

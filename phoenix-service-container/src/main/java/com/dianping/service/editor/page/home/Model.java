package com.dianping.service.editor.page.home;

import org.unidal.web.mvc.ViewModel;

import com.dianping.service.deployment.entity.DeploymentModel;
import com.dianping.service.editor.EditorPage;

public class Model extends ViewModel<EditorPage, Action, Context> {
	private DeploymentModel m_deployment;

	public Model(Context ctx) {
		super(ctx);
	}

	@Override
	public Action getDefaultAction() {
		return Action.VIEW;
	}

	public DeploymentModel getDeployment() {
		return m_deployment;
	}

	public void setDeployment(DeploymentModel deployment) {
		m_deployment = deployment;
	}
}

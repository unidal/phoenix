package com.dianping.phoenix.console.page.deploy;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;

import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.deploy.DeployExecutor;
import com.dianping.phoenix.deploy.DeployManager;
import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.deploy.model.entity.DeployModel;

public class Handler implements PageHandler<Context> {
	@Inject
	private DeployManager m_deployManager;

	@Inject
	private DeployExecutor m_deployExecutor;

	@Inject
	private JspViewer m_jspViewer;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "deploy")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		// display only, no action here
	}

	@Override
	@OutboundActionMeta(name = "deploy")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);
		Payload payload = ctx.getPayload();
		Action action = payload.getAction();

		model.setAction(action);
		model.setPage(ConsolePage.DEPLOY);

		switch (action) {
		case VIEW:
			try {
				showView(model, payload);
			} catch (Exception e) {
				ctx.addError("deploy.query", e);
			}

			m_jspViewer.view(ctx, model);
			break;
		case STATUS:
			try {
				showStatus(model, payload);
			} catch (Exception e) {
				e.printStackTrace();
				ctx.addError("deploy.status", e);
			}

			m_jspViewer.view(ctx, model);
			break;
		}
	}

	private void showStatus(Model model, Payload payload) {
		int id = payload.getId();
		DeployModel deployModel = m_deployExecutor.getModel(id);
		Map<String, Integer> progressMap = payload.getProgressMap();
		StatusModelVisitor visitor = new StatusModelVisitor(progressMap);

		deployModel.accept(visitor);
		model.setDeploy(visitor.getModel());
	}

	private void showView(Model model, Payload payload) throws Exception {
		int id = payload.getId();
		DeployModel deployModel = m_deployExecutor.getModel(id);
		Deployment deployment = m_deployManager.query(id);
		DeployPlan plan = new DeployPlan();

		plan.setVersion(deployment.getWarVersion());
		plan.setPolicy(deployment.getStrategy());
		plan.setAbortOnError("abortOnError".equals(deployment.getErrorPolicy()));

		model.setPlan(plan);
		model.setDeploy(deployModel);
	}
}

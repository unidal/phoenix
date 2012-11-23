package com.dianping.phoenix.console.page.deploy2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.deploy.DeployManager;
import com.dianping.phoenix.deploy.DeployPlan;

public class Handler implements PageHandler<Context> {
	@Inject
	private DeployManager m_deployManager;

	@Inject
	private JspViewer m_jspViewer;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "deploy2")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		// display only, no action here
	}

	@Override
	@OutboundActionMeta(name = "deploy2")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);
		Payload payload = ctx.getPayload();

		model.setAction(Action.VIEW);
		model.setPage(ConsolePage.DEPLOY2);

		try {
			Deployment deployment = m_deployManager.query(payload.getId());
			DeployPlan plan = new DeployPlan();

			plan.setVersion(deployment.getWarVersion());
			plan.setPolicy(deployment.getStrategy());
			plan.setAbortOnError("abortOnError".equals(deployment.getErrorPolicy()));
			model.setName(deployment.getDomain());
			model.setPlan(plan);

			List<String> hosts = new ArrayList<String>();

			// TODO
			model.setHosts(hosts);
		} catch (Exception e) {
			ctx.addError("deploy.query", e);
		}

		m_jspViewer.view(ctx, model);
	}
}

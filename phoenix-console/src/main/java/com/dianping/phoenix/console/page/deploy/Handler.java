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
import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.service.ProjectManager;

public class Handler implements PageHandler<Context> {
	@Inject
	private ProjectManager m_projectManager;

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
			showView(ctx, model, payload);
			break;
		case STATUS:
			showStatus(ctx, model, payload);
			break;
		}

		m_jspViewer.view(ctx, model);
	}

	private void showStatus(Context ctx, Model model, Payload payload) {
		int id = payload.getId();
		DeployModel deployModel = m_projectManager.findModel(id);

		if (deployModel == null) {
			ctx.addError("deploy.notFound", null);
			return;
		}

		try {
			Map<String, Integer> progressMap = payload.getProgressMap();
			StatusModelVisitor visitor = new StatusModelVisitor(progressMap);

			deployModel.accept(visitor);
			model.setDeploy(visitor.getModel());
		} catch (Exception e) {
			ctx.addError("deploy.error", e);
		}
	}

	private void showView(Context ctx, Model model, Payload payload) {
		int id = payload.getId();
		DeployModel deployModel = m_projectManager.findModel(id);

		if (deployModel == null) {
			ctx.addError("deploy.notFound", null);
			return;
		}

		try {
			ViewModelVisitor visitor = new ViewModelVisitor();

			deployModel.accept(visitor);

			model.setDeploy(visitor.getModel());
		} catch (Exception e) {
			ctx.addError("deploy.error", e);
		}
	}
}

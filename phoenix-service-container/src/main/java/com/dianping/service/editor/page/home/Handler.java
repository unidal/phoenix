package com.dianping.service.editor.page.home;

import java.io.IOException;

import javax.servlet.ServletException;

import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.service.deployment.entity.DeploymentModel;
import com.dianping.service.editor.EditorPage;
import com.dianping.service.editor.model.ModelBuilder;

public class Handler implements PageHandler<Context> {
	@Inject
	private JspViewer m_jspViewer;

	@Inject
	private ModelBuilder m_builder;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "home")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		// display only, no action here
	}

	@Override
	@OutboundActionMeta(name = "home")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);

		model.setAction(Action.VIEW);
		model.setPage(EditorPage.HOME);

		DeploymentModel deployment = new DeploymentModel();

		deployment.accept(m_builder);
		model.setDeployment(deployment);

		m_jspViewer.view(ctx, model);
	}
}

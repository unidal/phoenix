package com.dianping.phoenix.console.page.deploy;

import java.io.IOException;

import javax.servlet.ServletException;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.service.DeployService;
import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

public class Handler implements PageHandler<Context> {
	@Inject
	private JspViewer m_jspViewer;

	@Inject
	private DeployService m_service;

	private void getMessages(Model model, Payload payload) {
		StringBuilder sb = new StringBuilder(1024);
		String plan = payload.getPlan();
		int offset = payload.getOffset();
		int logs = m_service.getMessages(plan, offset, sb);

		model.setLog(sb.toString());
		model.setOffset(offset + logs);
	}

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "deploy")
	public void handleInbound(Context ctx) throws ServletException, IOException {
	}

	@Override
	@OutboundActionMeta(name = "deploy")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);
		Payload payload = ctx.getPayload();

		model.setAction(payload.getAction());
		model.setPage(ConsolePage.DEPLOY);

		String plan = payload.getPlan();
		
		switch (payload.getAction()) {
		case LOG:
			model.setHostPlans(m_service.getHostPlans(plan));
			model.setStatus(m_service.getStatus(plan));
			getMessages(model, payload);

			break;
		case VIEW:
			model.setHostPlans(m_service.getHostPlans(plan));
			model.setStatus(m_service.getStatus(plan));
			getMessages(model, payload);

			break;
		}

		m_jspViewer.view(ctx, model);
	}
}

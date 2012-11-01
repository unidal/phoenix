package com.dianping.kernel.console.page.home;

import java.io.IOException;

import javax.servlet.ServletException;

import com.dianping.kernel.console.ConsolePage;
import com.dianping.kernel.state.ApplicationModel;
import com.site.lookup.annotation.Inject;
import com.site.web.mvc.PageHandler;
import com.site.web.mvc.annotation.InboundActionMeta;
import com.site.web.mvc.annotation.OutboundActionMeta;
import com.site.web.mvc.annotation.PayloadMeta;

public class Handler implements PageHandler<Context> {
	@Inject
	private JspViewer m_jspViewer;

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
		ApplicationModel applicationModel = (ApplicationModel) ctx.getServletContext().getAttribute(
		      "phoenix.applicationModel");

		model.setAction(Action.VIEW);
		model.setPage(ConsolePage.HOME);
		model.setApplicationModel(applicationModel);

		m_jspViewer.view(ctx, model);
	}
}

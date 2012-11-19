package com.dianping.kernel.inspect.page.home;

import java.io.IOException;

import javax.servlet.ServletException;

import com.dianping.kernel.Constants;
import com.dianping.kernel.GlobalModel;
import com.dianping.kernel.inspect.InspectPage;
import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

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
		GlobalModel globalModel = (GlobalModel) ctx.getServletContext().getAttribute(Constants.PHOENIX_MODEL_GLOBAL);

		model.setAction(Action.VIEW);
		model.setPage(InspectPage.HOME);
		model.setApplicationModel(globalModel);

		m_jspViewer.view(ctx, model);
	}
}

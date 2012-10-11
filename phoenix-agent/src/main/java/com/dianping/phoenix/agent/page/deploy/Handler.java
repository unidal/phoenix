package com.dianping.phoenix.agent.page.deploy;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;

import com.dianping.phoenix.agent.page.deploy.shell.Shell;
import com.site.lookup.annotation.Inject;
import com.site.web.mvc.PageHandler;
import com.site.web.mvc.annotation.InboundActionMeta;
import com.site.web.mvc.annotation.OutboundActionMeta;
import com.site.web.mvc.annotation.PayloadMeta;

public class Handler implements PageHandler<Context> {
	@Inject
	private Shell m_shell;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "deploy")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		// display only, no action here
	}

	@Override
	@OutboundActionMeta(name = "deploy")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Payload payload = ctx.getPayload();

		OutputStream resOut = ctx.getHttpServletResponse().getOutputStream();
		
		switch (payload.getAction()) {
		case PREPARE:
			System.out.println();
			m_shell.prepare(payload.getVersion(), resOut);
			break;
		case ACTIVATE:
			m_shell.activate(resOut);
			break;
		case COMMIT:
			m_shell.commit(resOut);
			break;
		case ROLLBACK:
			m_shell.rollback(payload.getVersion(), resOut);
			break;
		default:
			break;
		}
	}
}

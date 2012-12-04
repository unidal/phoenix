package com.dianping.phoenix.console.page.version;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.phoenix.console.dal.deploy.Version;
import com.dianping.phoenix.service.VersionManager;

public class Handler implements PageHandler<Context> {
	@Inject
	private VersionManager m_manager;

	@Inject
	private JspViewer m_jspViewer;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "version")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		Payload payload = ctx.getPayload();

		if (payload.getAction() == Action.ADD) {
			String version = payload.getVersion();
			String description = payload.getDescription();
			String releaseNotes = "No release notes here";
			String createdBy = "phoenix";

			try {
				m_manager.createVersion(version, description, releaseNotes, createdBy);
			} catch (Exception e) {
				e.printStackTrace(); // TODO remove it
				ctx.addError("version.add", e);
			}
		} else if (payload.getAction() == Action.REMOVE) {
			int id = payload.getId();
			
			try {
				m_manager.removeVersion(id);
			} catch (Exception e) {
				ctx.addError("version.remove", e);
			}
		} else if (payload.getAction() == Action.STATUS) {
			String version = payload.getVersion();
			int index = payload.getIndex();
			
			
		}
	}

	@Override
	@OutboundActionMeta(name = "version")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);

		try {
			List<Version> versions = m_manager.getActiveVersions();

			model.setVersions(versions);
		} catch (Exception e) {
			ctx.addError("version.active", e);
		}
//		model.setAction(Action.VIEW);
//		model.setPage(ConsolePage.VERSION);

		m_jspViewer.view(ctx, model);
	}
}

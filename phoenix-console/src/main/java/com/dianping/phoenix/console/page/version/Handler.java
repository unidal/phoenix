package com.dianping.phoenix.console.page.version;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.dal.deploy.Version;

public class Handler implements PageHandler<Context> {
	@Inject
	private VersionManager m_manager;

	@Inject
	private JspViewer m_jspViewer;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "version")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		
	}

	@Override
	@OutboundActionMeta(name = "version")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);
		Payload payload = ctx.getPayload();
		Action action = payload.getAction();
		model.setAction(action);
		model.setPage(ConsolePage.VERSION);
		
		switch (action) {
			case CREATE:
				createVersion(ctx, payload, model);
				break;
			case REMOVE:
				removeVersion(ctx, payload, model);
				break;
			case STATUS:
				String version = payload.getVersion();
				int index = payload.getIndex();
				
				break;
			case VIEW:
				viewVersions(ctx, model);
				break;
		}

		m_jspViewer.view(ctx, model);
	}

	private void createVersion(Context ctx, Payload payload, Model model) {
		String version = payload.getVersion();
		String description = payload.getDescription();
		String releaseNotes = "No release notes here";
		String createdBy = "phoenix";
		try {
			m_manager.createVersion(version, description, releaseNotes, createdBy);
			ctx.redirect(getActionUri(ctx, ConsolePage.VERSION));
		} catch (Exception e) {
			e.printStackTrace(); // TODO remove it
			ctx.addError("version.create", e);
			model.setAction(Action.VIEW);
			viewVersions(ctx, model);
		}
	}

	private void removeVersion(Context ctx, Payload payload, Model model) {
		int id = payload.getId();
		try {
			m_manager.removeVersion(id);
			ctx.redirect(getActionUri(ctx, ConsolePage.VERSION));
		} catch (Exception e) {
			ctx.addError("version.remove", e);
			model.setAction(Action.VIEW);
			viewVersions(ctx, model);
		}
	}

	private void viewVersions(Context ctx, Model model) {
		try {
			List<Version> versions = m_manager.getFinishedVersions();
			model.setVersions(versions);
			Version activeVersion = m_manager.getActiveVersion();
			if (activeVersion != null) {
				model.setCreatingVersion(activeVersion.getVersion());
			}
//			model.setCreatingVersion("0.0.1-SNAPSHOT");
		} catch (Exception e) {
			ctx.addError("version.active", e);
		}
	}

	private String getActionUri(Context ctx, ConsolePage page) {
		return ctx.getRequestContext().getActionUri(page.getName());
	}
}

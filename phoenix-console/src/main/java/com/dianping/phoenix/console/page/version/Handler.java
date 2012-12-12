package com.dianping.phoenix.console.page.version;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.ErrorObject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.dal.deploy.Version;
import com.dianping.phoenix.version.VersionManager;
import com.dianping.phoenix.version.VersionManager.VersionLog;

public class Handler implements PageHandler<Context> {
    
    private static Log logger = LogFactory.getLog(Handler.class);
    
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
			showStatus(payload, model);
			break;
		case VIEW:
			viewVersions(ctx, model);
			break;
		case GET_VERSIONS:
			getVersions(ctx, model);
			break;
		}

		m_jspViewer.view(ctx, model);
	}

	private void getVersions(Context ctx, Model model) {
		try {
			model.setVersions(m_manager.getFinishedVersions());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void showStatus(Payload payload, Model model) {
		try {
			String version = payload.getVersion();
			int index = payload.getIndex();
			Version activeVersion = m_manager.getActiveVersion();
			if (activeVersion != null) {
				model.setCreatingVersion(activeVersion.getVersion());
			}

			VersionLog statusLog = m_manager.getStatus(version, index);
			if (statusLog != null) {
				List<String> messages = statusLog.getMessages();
				if (messages != null) {
					StringBuffer buffer = new StringBuffer();
					for (String message : messages) {
						if (message != null) {
							buffer.append(message.replace("\"", "\\\"").replace("\n", "<br>")).append("<br>");
						}
					}
					model.setLogcontent(buffer.toString());
				}
				model.setIndex(statusLog.getIndex());
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
		    logger.error("Create version[" + version + "] failed.", e);
			ctx.addError(new ErrorObject("version.create", e).addArgument("message", e.getMessage()));
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
		    logger.error("Remove version[id=" + id + "] failed.", e);
		    ctx.addError(new ErrorObject("version.remove", e).addArgument("message", e.getMessage()));
			model.setAction(Action.VIEW);
			viewVersions(ctx, model);
		}
	}

	private void viewVersions(Context ctx, Model model) {
		try {
			getVersions(ctx, model);
			Version activeVersion = m_manager.getActiveVersion();
			if (activeVersion != null) {
				model.setCreatingVersion(activeVersion.getVersion());
			}
		} catch (Exception e) {
		    logger.error("View versions failed.", e);
		    throw new RuntimeException(e);
		}
	}

	private String getActionUri(Context ctx, ConsolePage page) {
		return ctx.getRequestContext().getActionUri(page.getName());
	}
}

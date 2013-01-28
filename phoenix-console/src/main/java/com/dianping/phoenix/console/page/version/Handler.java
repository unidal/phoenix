package com.dianping.phoenix.console.page.version;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.unidal.dal.jdbc.DalNotFoundException;
import org.unidal.lookup.annotation.Inject;
import org.unidal.tuple.Ref;
import org.unidal.web.mvc.ErrorObject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.cat.Cat;
import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.dal.deploy.Deliverable;
import com.dianping.phoenix.deliverable.DeliverableManager;
import com.dianping.phoenix.deliverable.DeliverableStatus;
import com.dianping.phoenix.deliverable.LogService;

public class Handler implements PageHandler<Context>, LogEnabled {
	@Inject
	private DeliverableManager m_manager;

	@Inject
	private LogService m_logService;

	@Inject
	private JspViewer m_jspViewer;

	private Logger m_logger;

	private void createVersion(Context ctx, Payload payload) {
		String type = payload.getType();
		String version = payload.getVersion();
		String description = payload.getDescription();

		try {
			m_manager.createDeliverable(type, version, description);

			ctx.redirect(ConsolePage.VERSION, "warType=" + type);
		} catch (Exception e) {
			Cat.logError(e);
			m_logger.error(String.format("Failed to create version(%s) of %s!", version, type), e);

			ctx.addError(new ErrorObject("version.create", e));
			payload.setAction(Action.VIEW.getName());
		}
	}

	@Override
	public void enableLogging(Logger logger) {
		m_logger = logger;
	}

	private void getVersions(Context ctx, Model model, String warType) {
		try {
			model.setDeliverables(m_manager.getAllDeliverables(warType, DeliverableStatus.ACTIVE));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "version")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		if (!ctx.hasErrors()) {
			Payload payload = ctx.getPayload();
			Action action = payload.getAction();

			switch (action) {
			case CREATE:
				createVersion(ctx, payload);
				break;
			case REMOVE:
				removeVersion(ctx, payload);
				break;
			}
		}
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
		case STATUS:
			showStatus(ctx, model);
			break;
		case VIEW:
			showView(ctx, model);
			break;
		case GET_VERSIONS:
			getVersions(ctx, model, payload.getType());
			break;
		}

		m_jspViewer.view(ctx, model);
	}

	private void removeVersion(Context ctx, Payload payload) {
		String warType = payload.getType();
		String version = payload.getVersion();
		int id = payload.getId();

		try {
			m_manager.removeDeliverable(id);
			ctx.redirect(ConsolePage.VERSION, "warType=" + warType);
		} catch (Exception e) {
			m_logger.error(String.format("Failed to remove version(%s) of %s!", version, warType), e);
			ctx.addError(new ErrorObject("version.remove", e));
			payload.setAction(Action.VIEW.getName());
		}
	}

	private void showStatus(Context ctx, Model model) {
		Payload payload = ctx.getPayload();
		String warType = payload.getType();
		int index = payload.getIndex();

		try {
			String activeVersion = m_manager.getDeliverable(warType, DeliverableStatus.CREATIING).getWarVersion();
			Ref<Integer> ref = new Ref<Integer>(index);
			List<String> messages = m_logService.getMessages(warType + ":" + activeVersion, ref);

			model.setCreatingVersion(activeVersion);

			StringBuilder sb = new StringBuilder();

			for (String message : messages) {
				sb.append(message.replace("\"", "\\\"").replace("\n", "<br>")).append("<br>");
			}

			model.setLog(sb.toString());
			model.setIndex(ref.getValue());
		} catch (Exception e) {
			Cat.logError(e);
			ctx.addError("version.status", e);
		}
	}

	private void showView(Context ctx, Model model) {
		Payload payload = ctx.getPayload();
		String warType = payload.getType();

		try {
			model.setDeliverables(m_manager.getAllDeliverables(warType, DeliverableStatus.ACTIVE));

			try {
				Deliverable d = m_manager.getDeliverable(warType, DeliverableStatus.CREATIING);

				model.setCreatingVersion(d.getWarVersion());
			} catch (DalNotFoundException e) {
				// ignore it
			}
		} catch (Exception e) {
			Cat.logError(e);
			ctx.addError("version.view", e);
		}
	}
}

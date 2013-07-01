package com.dianping.phoenix.console.page.home;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.dal.deploy.Deliverable;
import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.deliverable.DeliverableManager;
import com.dianping.phoenix.deliverable.DeliverableStatus;
import com.dianping.phoenix.deploy.DeployManager;
import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.deploy.DeployPolicy;
import com.dianping.phoenix.project.entity.BussinessLine;
import com.dianping.phoenix.project.entity.Project;
import com.dianping.phoenix.service.DeviceManager;
import com.dianping.phoenix.service.ProjectManager;

public class Handler implements PageHandler<Context>, LogEnabled {
	@Inject
	private ProjectManager m_projectManager;
	
	@Inject
	private DeviceManager m_deviceManager;

	@Inject
	private DeliverableManager m_deliverableManager;

	@Inject
	private DeployManager m_deployManager;

	@Inject
	private JspViewer m_jspViewer;

	private Logger m_logger;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "home")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		Payload payload = ctx.getPayload();
		Action action = payload.getAction();

		if (action == Action.DEPLOY) {
			if (!ctx.hasErrors()) {
				String name = payload.getProject();
				String deployUri = ctx.getRequestContext().getActionUri(ConsolePage.DEPLOY.getName());

				if (payload.isDeploy()) {
					List<String> hosts = payload.getHosts();
					DeployPlan plan = payload.getPlan();

					try {
						String logUri = String.format("http://%s:%s%s?id=", //
								ctx.getHttpServletRequest().getServerName(),ctx.getHttpServletRequest().getServerPort(),deployUri);
						int id = m_deployManager.deploy(name, hosts, plan, logUri);
						ctx.redirect(deployUri + "?id=" + id);
						return;
					} catch (Exception e) {
						m_logger.warn(String.format("Error when submitting deploy to hosts(%s) for project(%s)! Error: %s.",
						      hosts, name, e));

						ctx.addError("project.deploy", e);
					}
				} else if (payload.isWatch()) {
					DeployPlan plan = payload.getPlan();

					try {
						Deployment deploy = m_projectManager.findActiveDeploy(plan.getWarType(), name);

						if (deploy != null) {
							ctx.redirect(deployUri + "?id=" + deploy.getId());
						}
						return;
					} catch (Exception e) {
						m_logger.warn(String.format("Error when finding active deploy id for project(%s)!", name), e);

						ctx.addError("project.watch", e);
					}
				}
			}

			// validation failed, back to project page
			payload.setAction(Action.PROJECT.getName());
		}
	}

	@Override
	@OutboundActionMeta(name = "home")
	public void handleOutbound(Context ctx) throws ServletException, IOException {
		Model model = new Model(ctx);
		Payload payload = ctx.getPayload();
		Action action = payload.getAction();

		model.setAction(payload.getAction());
		model.setPage(ConsolePage.HOME);

		switch (action) {
		case HOME:
			try {
				List<BussinessLine> bussinessLineList = m_deviceManager.getBussinessLineList();
				model.setBussinessLines(bussinessLineList);
			} catch (Exception e) {
				m_logger.warn(String.format("Error when searching projects with keyword(%s)!", payload.getKeyword()), e);
				ctx.addError("project.search", e);
			}
			break;
		case PROJECT:
			String name = payload.getProject();
			DeployPlan plan = payload.getPlan();

			try {
				String warType = plan.getWarType();
				Project project = m_deviceManager.getProjectByName(name);
				List<Deliverable> versions = m_deliverableManager.getAllDeliverables(warType, DeliverableStatus.ACTIVE);
				Deployment activeDeployment = m_projectManager.findActiveDeploy(warType, name);

				model.setProject(project);
				model.setDeliverables(versions);
				model.setPolicies(DeployPolicy.values());
				model.setActiveDeployment(activeDeployment);
			} catch (Exception e) {
				m_logger.warn(String.format("Error when finding project(%s)!", name), e);
				ctx.addError("project.view", e);
			}

			break;
		default:
			break;
		}

		m_jspViewer.view(ctx, model);
	}

	@Override
	public void enableLogging(Logger logger) {
		m_logger = logger;
	}
}

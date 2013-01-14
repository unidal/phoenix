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
import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.console.dal.deploy.Version;
import com.dianping.phoenix.deploy.DeployManager;
import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.deploy.DeployPolicy;
import com.dianping.phoenix.project.entity.Project;
import com.dianping.phoenix.service.ProjectManager;
import com.dianping.phoenix.version.VersionManager;

public class Handler implements PageHandler<Context>, LogEnabled {
	@Inject
	private ProjectManager m_projectManager;

	@Inject
	private VersionManager m_versionManager;

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
						int id = m_deployManager.deploy(name, hosts, plan);
						ctx.redirect(deployUri + "?id=" + id);
						return;
					} catch (Exception e) {
						m_logger.warn(
						      String.format("Error when submitting deploy to hosts(%s) for project(%s)! Error: %s.", hosts, name, e));

						ctx.addError("project.deploy", e);
					}
				} else if (payload.isWatch()) {
					try {
						Deployment deploy = m_projectManager.findActiveDeploy(name);

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
				List<Project> projects = m_projectManager.searchProjects(payload.getKeyword());

				model.setProjects(projects);
			} catch (Exception e) {
				m_logger.warn(String.format("Error when searching projects with keyword(%s)!", payload.getKeyword()), e);
				ctx.addError("project.search", e);
			}
			break;
		case PROJECT:
			String name = payload.getProject();

			try {
				Project project = m_projectManager.findProjectBy(name);
				List<Version> versions = m_versionManager.getFinishedVersions();
				Deployment activeDeployment = m_projectManager.findActiveDeploy(name);

				model.setProject(project);
				model.setVersions(versions);
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

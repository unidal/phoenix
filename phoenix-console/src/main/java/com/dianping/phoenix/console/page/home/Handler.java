package com.dianping.phoenix.console.page.home;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.dal.deploy.Version;
import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.deploy.DeployPolicy;
import com.dianping.phoenix.project.entity.Project;
import com.dianping.phoenix.service.ProjectManager;
import com.dianping.phoenix.service.VersionManager;

public class Handler implements PageHandler<Context> {
	@Inject
	private JspViewer m_jspViewer;

	@Inject
	private ProjectManager m_projectManager;

	@Inject
	private VersionManager m_versionManager;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "home")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		Payload payload = ctx.getPayload();
		Action action = payload.getAction();

		if (action == Action.DEPLOY) {
			if (!ctx.hasErrors()) {
				String name = payload.getProject();
				List<String> hosts = payload.getHosts();
				DeployPlan plan = payload.getPlan();
				String deployUri = ctx.getRequestContext().getActionUri(ConsolePage.DEPLOY.getName());

				try {
					int id = m_projectManager.deployToProject(name, hosts, plan);
					redirect(ctx, deployUri + "?id=" + id);
					return;
				} catch (Exception e) {
					e.printStackTrace();
					ctx.addError("project.deploy", e);
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
				ctx.addError("project.search", e);
			}
			break;
		case PROJECT:
			try {
				Project project = m_projectManager.findProjectBy(payload.getProject());
				List<Version> versions = m_versionManager.getActiveVersions();

				model.setProject(project);
				model.setVersions(versions);
				model.setPolicies(DeployPolicy.values());
			} catch (Exception e) {
				ctx.addError("project.view", e);
			}

			break;
		default:
			break;
		}

		m_jspViewer.view(ctx, model);
	}

	private void redirect(Context ctx, String url) {
		HttpServletResponse response = ctx.getHttpServletResponse();

		response.setHeader("location", url);
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		ctx.stopProcess();
	}
}

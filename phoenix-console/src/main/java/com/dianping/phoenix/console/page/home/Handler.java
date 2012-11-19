package com.dianping.phoenix.console.page.home;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import com.dianping.phoenix.console.ConsolePage;
import com.dianping.phoenix.console.model.entity.Project;
import com.dianping.phoenix.console.service.DeployService;
import com.dianping.phoenix.console.service.ProjectService;
import org.unidal.lookup.annotation.Inject;
import org.unidal.web.mvc.PageHandler;
import org.unidal.web.mvc.annotation.InboundActionMeta;
import org.unidal.web.mvc.annotation.OutboundActionMeta;
import org.unidal.web.mvc.annotation.PayloadMeta;

public class Handler implements PageHandler<Context> {
	@Inject
	private JspViewer m_jspViewer;

	@Inject
	private ProjectService m_projectService;

	@Inject
	private DeployService m_deployService;

	@Override
	@PayloadMeta(Payload.class)
	@InboundActionMeta(name = "home")
	public void handleInbound(Context ctx) throws ServletException, IOException {
		Payload payload = ctx.getPayload();
		Action action = payload.getAction();

		if (action == Action.DEPLOY) {
			List<String> hosts = payload.getHosts();
			String plan = payload.getPlan();
			String deployUri = ctx.getRequestContext().getActionUri(ConsolePage.DEPLOY.getName());

			m_deployService.deploy(hosts, plan);
			redirect(ctx, deployUri + "?plan=" + plan);
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
			List<Project> projects = m_projectService.search(payload.getKeyword());

			model.setProjects(projects);
			break;
		case PROJECT:
			Project project = m_projectService.findByName(payload.getProjectName());
			List<String> deployPlans = m_projectService.getPatches();

			model.setProject(project);
			model.setDeployPlans(deployPlans);
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

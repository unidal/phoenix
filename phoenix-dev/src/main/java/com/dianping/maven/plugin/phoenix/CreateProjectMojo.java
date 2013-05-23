package com.dianping.maven.plugin.phoenix;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.unidal.maven.plugin.common.PropertyProviders;

import com.dianping.maven.plugin.phoenix.model.entity.BizProject;
import com.dianping.maven.plugin.phoenix.model.entity.PhoenixProject;
import com.dianping.maven.plugin.phoenix.model.entity.Router;
import com.dianping.maven.plugin.phoenix.model.entity.Workspace;
import com.dianping.maven.plugin.tools.console.ConsoleIO;

/**
 * @goal create-project
 * @requiresProject false
 */
public class CreateProjectMojo extends AbstractMojo {

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		// select biz project
		List<String> availableValues = Arrays.asList(new String[] { "dpindex-web", "shop-web", "shoplist-web",
				"user-web", "user-service", "user-base-service" });

		List<String> bizProjects = new ArrayList<String>();
		try {
			bizProjects = new ConsoleIO().choice(availableValues, 3, "Which project(s) to checkout(separate by comma)");
		} catch (IOException e) {
			e.printStackTrace();
		}

		// select workspace dir
		String wsDir = PropertyProviders.fromConsole().forString("workspace-dir", "Directory to put code",
				new File(".").getAbsolutePath(), null);

		WorkspaceFacade wsFacade;
		try {
			PlexusContainer plexus = new DefaultPlexusContainer();
			wsFacade = plexus.lookup(WorkspaceFacade.class);
		} catch (Exception e) {
			throw new MojoFailureException("error lookup WorkspaceFacade from container", e);
		}

		Workspace model = new Workspace();
		model.setDir(wsDir);
		for (String bizProjectName : bizProjects) {
			BizProject bizProject = new BizProject();
			bizProject.setName(bizProjectName);
			model.addBizProject(bizProject);
		}

		PhoenixProject phoenixProject = new PhoenixProject();
		Router router = new Router();
		router.setDefaultUrlPattern("http://www.51ping.com%s");
		router.setPort(8080);
		router.setVersion("0.1-SNAPSHOT");
		phoenixProject.setRouter(router);
		model.setPhoenixProject(phoenixProject);

		try {
			wsFacade.create(model);
		} catch (Exception e) {
			throw new MojoFailureException("error create phoenix workspace", e);
		}

	}
}

package com.dianping.maven.plugin.phoenix;

import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainer;
import org.unidal.maven.plugin.common.PropertyProviders;

import com.dianping.maven.plugin.phoenix.model.entity.BizProject;
import com.dianping.maven.plugin.phoenix.model.entity.PhoenixProject;
import com.dianping.maven.plugin.phoenix.model.entity.Router;
import com.dianping.maven.plugin.phoenix.model.entity.Workspace;

/**
 * @goal create-project
 */
public class CreateProjectMojo extends AbstractMojo {
	/**
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject m_project;

	/**
	 * @parameter expression="${project.build.outputDirectory}" default-value=""
	 */
	private String m_targetDir;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		// select biz project
		List<String> availableValues = Arrays.asList(new String[] { "dpindex-web", "shop-web", "shoplist-web",
				"user-web", "user-service", "user-base-service" });
		String projectInput = PropertyProviders.fromConsole().forString("biz", "Choose projects:", availableValues,
				"dpindex-web", null);
		String[] bizProjects = projectInput.split(",");

		// select workspace dir
		String wsDir = PropertyProviders.fromConsole().forString("workspace-dir", "", ".", null);

		WorkspaceFacade wsFacade;
		try {
			PlexusContainer plexus = new DefaultPlexusContainer();
			wsFacade = plexus.lookup(WorkspaceFacade.class);
		} catch (Exception e) {
			throw new MojoFailureException("error lookup WorkspaceFacade from container", e);
		}

		Workspace model = new Workspace();
		model.setDir(wsDir);

		for (int i = 0; i < bizProjects.length; i++) {
			BizProject bizProject = new BizProject();
			bizProject.setName(bizProjects[i]);
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

		getLog().info(projectInput);
	}
}

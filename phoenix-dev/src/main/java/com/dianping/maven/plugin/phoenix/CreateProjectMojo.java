package com.dianping.maven.plugin.phoenix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.unidal.maven.plugin.common.PropertyProviders;

import com.dianping.maven.plugin.phoenix.model.entity.BizProject;
import com.dianping.maven.plugin.phoenix.model.entity.GitConf;
import com.dianping.maven.plugin.phoenix.model.entity.PhoenixProject;
import com.dianping.maven.plugin.phoenix.model.entity.Router;
import com.dianping.maven.plugin.phoenix.model.entity.Workspace;
import com.dianping.maven.plugin.tools.console.ConsoleIO;
import com.dianping.maven.plugin.tools.remedy.PomRemedy;

/**
 * @goal create-project
 * @requiresProject false
 */
public class CreateProjectMojo extends AbstractMojo {
	/**
	 * @component
	 */
	private WorkspaceFacade m_wsFacade;
	
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
				System.getProperty("user.dir"), null);

		Workspace model = buildModel(bizProjects, wsDir);

		try {
			m_wsFacade.create(model);
		} catch (Exception e) {
			throw new MojoFailureException("error create phoenix workspace", e);
		}

		try {
			PomRemedy.main(new String[] { model.getDir() });
		} catch (Exception e) {
			throw new MojoFailureException("error remedy pom", e);
		}

	}

	private Workspace buildModel(List<String> bizProjects, String wsDir) {
		Workspace model = new Workspace();
		model.setDir(wsDir);
		for (String bizProjectName : bizProjects) {
			BizProject bizProject = new BizProject();
			bizProject.setName(bizProjectName);
			model.addBizProject(bizProject);
		}

		PhoenixProject phoenixProject = new PhoenixProject();
		// router
		Router router = new Router();
		router.setDefaultUrlPattern("http://w.51ping.com%s");
		router.setPort(8080);
		router.setVersion("0.1-SNAPSHOT");
		phoenixProject.setRouter(router);
		
		// git conf
		GitConf gitConf = new GitConf();
		gitConf.setBranch("master");
		gitConf.setRepositoryUrl("http://code.dianpingoa.com/arch/phoenix-maven-config.git");
		phoenixProject.setGitConf(gitConf);

		model.setPhoenixProject(phoenixProject);
		return model;
	}
}

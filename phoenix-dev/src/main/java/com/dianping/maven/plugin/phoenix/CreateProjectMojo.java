package com.dianping.maven.plugin.phoenix;

import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.unidal.maven.plugin.common.PropertyProviders;

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
		List<String> availableValues = Arrays.asList(new String[]{"a", "b"});
		String value = PropertyProviders.fromConsole().forString("x", "xprompt", availableValues , "D", null);
		getLog().info(value);
	}
}

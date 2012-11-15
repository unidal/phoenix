package com.dianping.phoenix.console.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.dianping.phoenix.console.model.entity.Project;
import com.dianping.phoenix.console.model.entity.Projects;
import com.dianping.phoenix.console.model.transform.DefaultSaxParser;

public class DefaultProjectService implements ProjectService, Initializable {
	private Projects m_projects;

	@Override
	public Project findByName(String projectName) {
		for (Project project : m_projects.getProjects()) {
			if (project.getName().equalsIgnoreCase(projectName)) {
				return project;
			}
		}
		return null;
	}

	@Override
	public List<String> getPatches() {
		return m_projects.getPatches();
	}

	@Override
	public List<Project> search(String keyword) {
		if (keyword == null || keyword.length() == 0) {
			return m_projects.getProjects();
		} else {
			List<Project> list = new ArrayList<Project>();
			String lowercase = keyword.toLowerCase();

			for (Project project : m_projects.getProjects()) {
				List<String> jars = project.getDependencyJars();

				for (String jar : jars) {
					if (jar.toLowerCase().contains(lowercase)) {
						list.add(project);
					}
				}
			}

			return list;
		}
	}

	@Override
	public void initialize() throws InitializationException {
		InputStream in = getClass().getResourceAsStream("/projects.xml");

		if (in == null) {
			throw new RuntimeException("Resource(/projects.xml) is not found at classpath!");
		}

		try {
			m_projects = DefaultSaxParser.parse(in);
		} catch (Exception e) {
			throw new RuntimeException("Error when loading resource(/projects.xml).");
		}
	}
}

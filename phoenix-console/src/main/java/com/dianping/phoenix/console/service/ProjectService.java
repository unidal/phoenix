/**
 * Project: egret-home
 * 
 * File Created at 2012-9-7
 * 
 * Copyright 2012 dianping.com.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Dianping Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with dianping.com.
 */
package com.dianping.phoenix.console.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.dianping.phoenix.console.model.entity.Project;
import com.dianping.phoenix.console.model.entity.Projects;
import com.dianping.phoenix.console.model.transform.DefaultSaxParser;


/**
 * @author <a href="mailto:yiming.liu@dianping.com">Yiming Liu</a>
 */
public class ProjectService implements Initializable {
	private static Projects m_projects;

	public Project findByName(String projectName) {
		for (Project project : m_projects.getProjects()) {
			if (project.getName().equalsIgnoreCase(projectName)) {
				return project;
			}
		}
		return null;
	}
	
	public List<String> getPatches() {
		return m_projects.getPatches();
	}

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

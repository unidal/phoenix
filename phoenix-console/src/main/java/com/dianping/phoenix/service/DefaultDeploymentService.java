package com.dianping.phoenix.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.dianping.phoenix.deploy.entity.Deploy;
import com.dianping.phoenix.deploy.entity.Project;
import com.dianping.phoenix.deploy.transform.DefaultSaxParser;

public class DefaultDeploymentService implements DeploymentService, Initializable {
	private Deploy m_deploy;

	@Override
	public List<Project> search(String keyword) {
		List<Project> list = new ArrayList<Project>();

		if (keyword == null || keyword.trim().length() == 0) {
			list.addAll(m_deploy.getProjects().values());
		} else {
			for (Map.Entry<String, Project> e : m_deploy.getProjects().entrySet()) {
				if (e.getKey().contains(keyword)) {
					list.add(e.getValue());
				}
			}
		}

		return list;
	}

	@Override
	public Project findByName(String name) {
		return m_deploy.findProject(name);
	}

	@Override
	public int deploy(List<String> hosts, DeploymentPlan plan) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void initialize() throws InitializationException {
		// TODO test purpose
		InputStream in = getClass().getResourceAsStream("/com/dianping/phoenix/deploy/deploy.xml");

		try {
			m_deploy = DefaultSaxParser.parse(in);
		} catch (Exception e) {
			throw new RuntimeException(
			      "Unable to load deploy model from resource(com/dianping/phoenix/deploy/deploy.xml)!", e);
		}
	}
}

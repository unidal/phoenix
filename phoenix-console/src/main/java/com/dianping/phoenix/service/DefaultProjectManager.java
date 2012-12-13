package com.dianping.phoenix.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;

import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.project.entity.Project;
import com.dianping.phoenix.project.entity.Root;
import com.dianping.phoenix.project.transform.DefaultSaxParser;

public class DefaultProjectManager implements ProjectManager, Initializable {
	private Root m_root;

	private Map<String, DeployModel> m_models = new HashMap<String, DeployModel>();

	@Override
	public Integer findActiveDeployId(String name) {
		DeployModel model = m_models.get(name);

		if (model != null && isActive(model.getStatus())) {
			return model.getId();
		} else {
			return null;
		}
	}

	private boolean isActive(String status) {
		return "doing".equals(status) || "pending".equals(status);
	}

	@Override
	public DeployModel findModel(int deployId) {
		for (DeployModel model : m_models.values()) {
			if (model.getId() == deployId) {
				return model;
			}
		}

		return null;
	}

	@Override
	public Project findProjectBy(String name) throws Exception {
		return m_root.findProject(name);
	}

	@Override
	public void initialize() throws InitializationException {
		// TODO test purpose
		InputStream in = getClass().getResourceAsStream("/com/dianping/phoenix/deploy/project.xml");

		try {
			m_root = DefaultSaxParser.parse(in);
		} catch (Exception e) {
			throw new RuntimeException(
			      "Unable to load deploy model from resource(com/dianping/phoenix/deploy/project.xml)!", e);
		}
	}

	@Override
	public List<Project> searchProjects(String keyword) throws Exception {
		List<Project> list = new ArrayList<Project>();

		if (keyword == null || keyword.trim().length() == 0) {
			list.addAll(m_root.getProjects().values());
		} else {
			for (Map.Entry<String, Project> e : m_root.getProjects().entrySet()) {
				if (e.getKey().contains(keyword)) {
					list.add(e.getValue());
				}
			}
		}

		return list;
	}

	@Override
	public void storeModel(DeployModel model) {
		m_models.put(model.getDomain(), model);
	}
}

package com.dianping.phoenix.deploy;

import java.util.List;

import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.deploy.event.DeployListener;
import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.service.ProjectManager;

public class DefaultDeployManager extends ContainerHolder implements DeployManager {
	@Inject
	private ProjectManager m_projectManager;

	@Inject
	private DeployListener m_deployListener;

	private void check(String domain) {
		Deployment deploy = m_projectManager.findActiveDeploy(domain);

		if (deploy != null) {
			throw new RuntimeException(String.format("Project(%s) is being rolling out!", domain));
		}
	}

	@Override
	public int deploy(String domain, List<String> hosts, DeployPlan plan) throws Exception {
		check(domain);

		DeployExecutor executor = lookup(DeployExecutor.class, plan.getPolicy());
		DeployModel model = m_deployListener.onCreate(domain, hosts, plan);

		executor.submit(model, hosts);
		return model.getId();
	}
}

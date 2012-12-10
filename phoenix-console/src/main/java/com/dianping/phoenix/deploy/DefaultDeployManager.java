package com.dianping.phoenix.deploy;

import java.util.List;

import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.deploy.event.DeployListener;
import com.dianping.phoenix.deploy.model.entity.DeployModel;

public class DefaultDeployManager extends ContainerHolder implements DeployManager {
	@Inject
	private DeployListener m_listener;

	@Override
	public int deploy(String name, List<String> hosts, DeployPlan plan) throws Exception {
		DeployExecutor executor = lookup(DeployExecutor.class, plan.getPolicy());
		DeployModel model = m_listener.onCreate(name, hosts, plan);
		int deployId = model.getId();

		executor.submit(deployId, hosts);
		return deployId;
	}

	@Override
	public DeployModel getModel(int deployId) {
		return m_listener.getModel(deployId);
	}
}

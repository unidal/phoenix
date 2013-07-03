package com.dianping.phoenix.deploy.internal;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.deploy.DeployExecutor;
import com.dianping.phoenix.deploy.DeployListener;
import com.dianping.phoenix.deploy.DeployManager;
import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.deploy.DeployStatus;
import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.service.ProjectManager;

public class DefaultDeployManager extends ContainerHolder implements DeployManager, Initializable {
	@Inject
	private ProjectManager m_projectManager;

	@Inject
	private DeployListener m_deployListener;

	private List<DeployExecutor> m_deployExecutors;

	private void check(String type, String domain) {
		Deployment deploy = m_projectManager.findActiveDeploy(type, domain);

		if (deploy != null) {
			throw new RuntimeException(String.format("Project(%s) is being rolling out!", domain));
		}
	}

	@Override
	public int deploy(String domain, List<String> hosts, DeployPlan plan, String logUri) throws Exception {
		check(plan.getWarType(), domain);

		DeployExecutor executor = lookup(DeployExecutor.class, plan.getPolicy());
		DeployModel model = m_deployListener.onCreate(domain, hosts, plan);

		executor.submit(model, hosts, plan.getWarType(), logUri);
		return model.getId();
	}

	@Override
	public void deployOld(int deployId) {
		DeployModel model = m_projectManager.findModel(deployId);
		DeployStatus status = DeployStatus.getByName(model.getStatus(), null);
		if (model != null && !DeployStatus.isFinalStatus(status)) {
			DeployExecutor executor = lookup(DeployExecutor.class, model.getPlan().getPolicy());
			executor.submitOld(model);
		}
	}

	private DeployExecutor getExecutor(int deployId) {
		for (DeployExecutor e : m_deployExecutors) {
			if (e.isDeploying(deployId)) {
				return e;
			}
		}
		return null;
	}

	@Override
	public boolean pauseDeploy(int deployId) {
		DeployModel model = m_projectManager.findModel(deployId);
		if (model != null && !DeployStatus.SUCCESS.getName().equals(model.getStatus())
				&& !DeployStatus.WARNING.getName().equals(model.getStatus())) {
			model.setStatus(DeployStatus.PAUSING.getName());
			try {
				m_deployListener.onDeployPause(deployId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean cancelRestRollout(int deployId) {
		DeployModel model = m_projectManager.findModel(deployId);
		if (model != null && !DeployStatus.SUCCESS.getName().equals(model.getStatus())
				&& !DeployStatus.WARNING.getName().equals(model.getStatus())) {
			model.setStatus(DeployStatus.CANCELLING.getName());
			try {
				m_deployListener.onDeployCancel(deployId);
				DeployExecutor e = getExecutor(deployId);
				if (e != null) {
					e.continueDeploy(deployId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean continueDeploy(int deployId) {
		DeployExecutor e = getExecutor(deployId);
		if (e != null) {
			try {
				m_deployListener.onDeployContinue(deployId);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			e.continueDeploy(deployId);
			return true;
		} else {
			deployOld(deployId);
		}
		return false;
	}

	@Override
	public void initialize() throws InitializationException {
		m_deployExecutors = new ArrayList<DeployExecutor>(lookupList(DeployExecutor.class));
	}
}

package com.dianping.phoenix.deploy;

import java.util.Date;
import java.util.List;

import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.console.dal.deploy.DeploymentDao;
import com.dianping.phoenix.console.dal.deploy.DeploymentEntity;

public class DefaultDeployManager implements DeployManager {
	@Inject
	private DeploymentDao m_deploymentDao;

	@Override
	public int deploy(String name, List<String> hosts, DeployPlan plan) throws Exception {
		Deployment d = m_deploymentDao.createLocal();

		d.setDomain(name);
		d.setStrategy(plan.getPolicy());
		d.setErrorPolicy(plan.isAbortOnError() ? "abortOnError" : "fall-through");
		d.setBeginDate(new Date());
		d.setStatus(1); // 1 - created
		d.setDeployedBy("phoenix"); // TODO use real user name
		d.setWarVersion(plan.getVersion());
		d.setWarType(0); // 0 - kernel, 1 - application

		int deployId = m_deploymentDao.insert(d);
		return deployId;
	}

	@Override
	public Deployment query(int deployId) throws Exception {
		Deployment d = m_deploymentDao.findByPK(deployId, DeploymentEntity.READSET_FULL);

		return d;
	}
}

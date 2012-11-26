package com.dianping.phoenix.deploy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.unidal.lookup.ContainerHolder;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.console.dal.deploy.DeploymentDao;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetails;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsDao;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsEntity;
import com.dianping.phoenix.console.dal.deploy.DeploymentEntity;

public class DefaultDeployManager extends ContainerHolder implements DeployManager {
	@Inject
	private DeploymentDao m_deploymentDao;

	@Inject
	private DeploymentDetailsDao m_deploymentDetailsDao;

	private Deployment createDeployment(String name, DeployPlan plan) {
		Deployment d = m_deploymentDao.createLocal();

		d.setDomain(name);
		d.setStrategy(plan.getPolicy());
		d.setErrorPolicy(plan.isAbortOnError() ? "abortOnError" : "fall-through");
		d.setBeginDate(new Date());
		d.setStatus(1); // 1 - created
		d.setDeployedBy("phoenix"); // TODO use real user name
		d.setWarVersion(plan.getVersion());
		d.setWarType(0); // 0 - kernel, 1 - application

		return d;
	}

	private DeploymentDetails createDeploymentDetails(int deployId, String host, DeployPlan plan, String appVersion) {
		DeploymentDetails d = m_deploymentDetailsDao.createLocal();

		d.setDeploymentId(deployId);
		d.setIpAddress(host);
		d.setKernelVersion(plan.getVersion());
		d.setAppVersion(appVersion);
		d.setStatus(1); // 1 - created

		return d;
	}

	@Override
	public int deploy(String name, List<String> hosts, DeployPlan plan) throws Exception {
		Deployment d = createDeployment(name, plan);

		m_deploymentDao.insert(d);

		int deployId = d.getId();
		List<DeploymentDetails> detailsList = new ArrayList<DeploymentDetails>(hosts.size());
		for (String host : hosts) {
			DeploymentDetails details = createDeploymentDetails(deployId, host, plan, "TBD");// TODO

			detailsList.add(details);
		}

		m_deploymentDetailsDao.insert(detailsList.toArray(new DeploymentDetails[0]));

		DeployExecutor executor = lookup(DeployExecutor.class, plan.getPolicy());

		executor.submit(deployId, name, hosts, plan.getVersion(), plan.isAbortOnError());

		return deployId;
	}

	@Override
	public DeployUpdate poll(DeployContext ctx) {
		try {
			int deployId = ctx.getDeployId();
			Deployment d = m_deploymentDao.findByPK(deployId, DeploymentEntity.READSET_FULL);
			DeployExecutor executor = lookup(DeployExecutor.class, d.getStrategy());

			return executor.poll(ctx);
		} catch (Exception e) {
			return new DeployUpdate(true);
		}
	}

	@Override
	public Deployment query(int deployId) throws Exception {
		Deployment d = m_deploymentDao.findByPK(deployId, DeploymentEntity.READSET_FULL);
		List<DeploymentDetails> detailsList = m_deploymentDetailsDao.findAllByDeployId(deployId,
		      DeploymentDetailsEntity.READSET_FULL);

		d.getDetailsList().addAll(detailsList);
		return d;
	}
}

package com.dianping.phoenix.deploy.event;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.console.dal.deploy.DeploymentDao;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetails;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsDao;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsEntity;
import com.dianping.phoenix.console.dal.deploy.DeploymentEntity;
import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.deploy.model.entity.DeployModel;

public class DefaultDeployListener implements DeployListener {
	@Inject
	private DeploymentDao m_deploymentDao;

	@Inject
	private DeploymentDetailsDao m_deploymentDetailsDao;

	private Map<Integer, DeployModel> m_models = new HashMap<Integer, DeployModel>();

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
	public DeployModel getModel(int deployId) {
		return m_models.get(deployId);
	}

	@Override
	public int onDeployCreate(String name, List<String> hosts, DeployPlan plan) throws Exception {
		Deployment d = createDeployment(name, plan);

		m_deploymentDao.insert(d);

		int deployId = d.getId();
		List<DeploymentDetails> detailsList = new ArrayList<DeploymentDetails>(hosts.size());
		for (String host : hosts) {
			DeploymentDetails details = createDeploymentDetails(deployId, host, plan, "TBD");// TODO

			detailsList.add(details);
		}

		m_deploymentDetailsDao.insert(detailsList.toArray(new DeploymentDetails[0]));

		return deployId;
	}

	@Override
	public void onDeployEnd(int deployId) throws Exception {
		Deployment d = m_deploymentDao.createLocal();
		List<DeploymentDetails> list = m_deploymentDetailsDao.findAllByDeployId(deployId,
		      DeploymentDetailsEntity.READSET_STATUS);
		int status = 3; // 3 - completed with all successful, 4 - completed with partial failures

		for (DeploymentDetails details : list) {
			if (details.getStatus() != 3) {
				status = 4;
				break;
			}
		}

		d.setKeyId(deployId);
		d.setStatus(status);
		d.setEndDate(new Date());

		m_deploymentDao.updateByPK(d, DeploymentEntity.UPDATESET_STATUS);
	}

	@Override
	public void onDeployStart(DeployModel model) throws Exception {
		int deployId = model.getId();
		Deployment d = m_deploymentDao.createLocal();

		d.setKeyId(deployId);
		d.setStatus(2); // 2 - deploying
		d.setBeginDate(new Date());

		m_deploymentDao.updateByPK(d, DeploymentEntity.UPDATESET_STATUS);
		m_models.put(deployId, model);
	}
}

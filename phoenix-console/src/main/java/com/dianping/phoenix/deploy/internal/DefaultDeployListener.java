package com.dianping.phoenix.deploy.internal;

import java.util.Date;
import java.util.List;

import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.console.dal.deploy.DeploymentDao;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetails;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsDao;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsEntity;
import com.dianping.phoenix.console.dal.deploy.DeploymentEntity;
import com.dianping.phoenix.deploy.DeployConstant;
import com.dianping.phoenix.deploy.DeployListener;
import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.deploy.DeployStatus;
import com.dianping.phoenix.deploy.agent.AgentStatus;
import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.deploy.model.entity.HostModel;
import com.dianping.phoenix.deploy.model.entity.SegmentModel;
import com.dianping.phoenix.service.ProjectManager;

public class DefaultDeployListener implements DeployListener {
	@Inject
	private DeploymentDao m_deploymentDao;

	@Inject
	private DeploymentDetailsDao m_deploymentDetailsDao;

	@Inject
	private ProjectManager m_projectManager;

	private Deployment createDeployment(String domain, DeployPlan plan) {
		Deployment d = m_deploymentDao.createLocal();

		d.setDomain(domain);
		d.setStrategy(plan.getPolicy());
		d.setErrorPolicy(plan.isAbortOnError() ? 1 : 2);
		d.setSkipTest(plan.isSkipTest() ? 1 : 2);
		d.setAutoContinue(plan.isAutoContinue() ? 1 : 2);
		d.setDeployInterval(plan.getDeployInterval());
		d.setBeginDate(new Date());
		d.setStatus(1); // 1 - created
		d.setDeployedBy("phoenix"); // TODO use real user name
		d.setWarVersion(plan.getVersion());
		d.setWarType(plan.getWarType()); // kernel, agent

		return d;
	}

	private DeploymentDetails createDeploymentDetails(int deployId, String host, DeployPlan plan, String appVersion) {
		DeploymentDetails d = m_deploymentDetailsDao.createLocal();

		d.setDeployId(deployId);
		d.setIpAddress(host);
		d.setKernelVersion(plan.getVersion());
		d.setAppVersion(appVersion);
		d.setStatus(1); // 1 - created

		return d;
	}

	@Override
	public DeployModel onCreate(String domain, List<String> hosts, DeployPlan plan) throws Exception {
		DeployModel model = new DeployModel();
		Deployment d = createDeployment(domain, plan);

		m_deploymentDao.insert(d);

		int deployId = d.getId();

		for (String host : hosts) {
			DeploymentDetails details = createDeploymentDetails(d.getId(), host, plan, "TBD"); // TODO

			m_deploymentDetailsDao.insert(details);
			model.addHost(new HostModel(host).setId(details.getId()));
		}

		// for "summary" host
		DeploymentDetails details = createDeploymentDetails(d.getId(), DeployConstant.SUMMARY, plan, "TBD"); // TODO

		m_deploymentDetailsDao.insert(details);
		model.addHost(new HostModel(DeployConstant.SUMMARY).setId(details.getId()));

		model.setId(deployId);
		model.setDomain(domain);
		model.setPlan(plan);
		model.setAbortOnError(plan.isAbortOnError());
		model.setVersion(plan.getVersion());
		model.setSkipTest(plan.isSkipTest());

		m_projectManager.storeModel(model);
		return model;
	}

	@Override
	public void onDeployEnd(int deployId) throws Exception {
		Deployment d = m_deploymentDao.createLocal();
		List<DeploymentDetails> list = m_deploymentDetailsDao.findAllByDeployId(deployId,
				DeploymentDetailsEntity.READSET_FULL);
		DeployStatus status = DeployStatus.SUCCESS;
		DeploymentDetails s = null;

		for (DeploymentDetails details : list) {
			if (!DeployConstant.SUMMARY.equals(details.getIpAddress())
					&& details.getStatus() != AgentStatus.SUCCESS.getId()) {
				status = DeployStatus.WARNING;
			} else if (DeployConstant.SUMMARY.equals(details.getIpAddress())) {
				s = details;
			}
		}

		if (s == null) {
			throw new RuntimeException(String.format("Internal error: no summary record found for deploy(%s)!",
					deployId));
		}

		HostModel summaryHost = m_projectManager.findModel(deployId).findHost(DeployConstant.SUMMARY);
		String rawLog = new DeployModel().addHost(summaryHost).toString();

		s.setEndDate(new Date());
		s.setStatus(status.getId());
		s.setRawLog(rawLog);

		d.setKeyId(deployId);
		d.setStatus(status.getId());
		d.setEndDate(new Date());

		m_deploymentDetailsDao.updateByPK(s, DeploymentDetailsEntity.UPDATESET_STATUS);
		m_deploymentDao.updateByPK(d, DeploymentEntity.UPDATESET_STATUS);

		DeployModel model = m_projectManager.findModel(deployId);
		if (model != null) {
			model.setStatus(status.getName());
		}
	}

	@Override
	public void onDeployStart(int deployId) throws Exception {
		DeployModel model = m_projectManager.findModel(deployId);
		if (model != null) {
			model.setStatus(DeployStatus.DEPLOYING.getName());
		} else {
			throw new RuntimeException(String.format("Can not find deploy model for deployId: %d", deployId));
		}

		Deployment d = m_deploymentDao.createLocal();

		d.setKeyId(deployId);
		d.setStatus(DeployStatus.DEPLOYING.getId());

		m_deploymentDao.updateByPK(d, DeploymentEntity.UPDATESET_STATUS);
	}

	@Override
	public void onHostCancel(int deployId, String host) throws Exception {
		DeployModel deployModel = m_projectManager.findModel(deployId);
		HostModel hostModel = deployModel.findHost(host);

		hostModel.addSegment(new SegmentModel().setCurrentTicks(100).setTotalTicks(100) //
				.setStatus(AgentStatus.CANCELLED.getName()).setStep(AgentStatus.CANCELLED.getTitle()));

		DeploymentDetails details = m_deploymentDetailsDao.createLocal();
		String rawLog = new DeployModel().addHost(hostModel).toString();

		details.setStatus(AgentStatus.CANCELLED.getId()); // 9 - cancelled
		details.setKeyId(hostModel.getId());
		details.setEndDate(new Date());
		details.setRawLog(rawLog);
		m_deploymentDetailsDao.updateByPK(details, DeploymentDetailsEntity.UPDATESET_STATUS);
	}

	@Override
	public void onHostEnd(int deployId, String host) throws Exception {
		DeployModel deployModel = m_projectManager.findModel(deployId);
		HostModel hostModel = deployModel.findHost(DeployConstant.SUMMARY);
		AgentStatus status = AgentStatus.getByName(hostModel.getStatus(), null);

		// flush the summary log
		DeploymentDetails details = m_deploymentDetailsDao.createLocal();
		String rawLog = new DeployModel().addHost(hostModel).toString();

		if (status == AgentStatus.SUCCESS || status == AgentStatus.FAILED || status == AgentStatus.DEPLOYING) {
			details.setStatus(status.getId());
		} else {
			throw new RuntimeException(String.format("Internal error: unknown status(%s) of host(%s) of deploy(%s)!",
					status, host, deployId));
		}

		details.setKeyId(hostModel.getId());
		details.setEndDate(new Date());
		details.setRawLog(rawLog);
		m_deploymentDetailsDao.updateByPK(details, DeploymentDetailsEntity.UPDATESET_STATUS);
	}

	@Override
	public void onDeployPause(int deployId) throws Exception {
		Deployment d = m_deploymentDao.createLocal();

		d.setKeyId(deployId);
		d.setStatus(DeployStatus.PAUSING.getId());

		m_deploymentDao.updateByPK(d, DeploymentEntity.UPDATESET_STATUS);
	}

	@Override
	public void onDeployContinue(int deployId) throws Exception {
		Deployment d = m_deploymentDao.createLocal();

		d.setKeyId(deployId);
		d.setStatus(DeployStatus.DEPLOYING.getId());

		m_deploymentDao.updateByPK(d, DeploymentEntity.UPDATESET_STATUS);
	}

	@Override
	public void onDeployCancel(int deployId) throws Exception {
		Deployment d = m_deploymentDao.createLocal();

		d.setKeyId(deployId);
		d.setStatus(DeployStatus.CANCELLING.getId());

		m_deploymentDao.updateByPK(d, DeploymentEntity.UPDATESET_STATUS);
	}
}

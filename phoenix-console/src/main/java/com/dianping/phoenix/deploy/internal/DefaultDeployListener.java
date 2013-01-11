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
		d.setErrorPolicy(plan.isAbortOnError() ? "abortOnError" : "fallThrough");
		//TODO d.setSkipTest()
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
		model.setVersion(plan.getVersion());
		model.setAbortOnError(plan.isAbortOnError());
		model.setSkipTest(plan.isSkipTest());
		model.setPlan(plan);

		m_projectManager.storeModel(model);
		return model;
	}

	@Override
	public void onDeployEnd(int deployId) throws Exception {
		Deployment d = m_deploymentDao.createLocal();
		List<DeploymentDetails> list = m_deploymentDetailsDao.findAllByDeployId(deployId,
		      DeploymentDetailsEntity.READSET_FULL);
		int status = 3; // 3 - completed with all successful, 4 - completed with partial failures
		DeploymentDetails s = null;

		for (DeploymentDetails details : list) {
			if (details.getStatus() != 3) {
				status = 4;
				break;
			}
		}

		for (DeploymentDetails details : list) {
			if (DeployConstant.SUMMARY.equals(details.getIpAddress())) {
				s = details;
				break;
			}
		}

		if (s == null) {
			throw new RuntimeException(String.format("Internal error: no summary record found for deploy(%s)!", deployId));
		}

		HostModel summaryHost = m_projectManager.findModel(deployId).findHost(DeployConstant.SUMMARY);
		String rawLog = new DeployModel().addHost(summaryHost).toString();

		s.setEndDate(new Date());
		s.setStatus(status);
		s.setRawLog(rawLog);

		d.setKeyId(deployId);
		d.setStatus(status);
		d.setEndDate(new Date());

		m_deploymentDetailsDao.updateByPK(s, DeploymentDetailsEntity.UPDATESET_STATUS);
		m_deploymentDao.updateByPK(d, DeploymentEntity.UPDATESET_STATUS);
	}

	@Override
	public void onDeployStart(int deployId) throws Exception {
		Deployment d = m_deploymentDao.createLocal();

		d.setKeyId(deployId);
		d.setStatus(2); // 2 - deploying

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

		details.setStatus(9); // 9 - cancelled
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
}

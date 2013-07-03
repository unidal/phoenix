package com.dianping.phoenix.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.dal.jdbc.DalNotFoundException;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.console.dal.deploy.DeploymentDao;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetails;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsDao;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsEntity;
import com.dianping.phoenix.console.dal.deploy.DeploymentEntity;
import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.deploy.DeployStatus;
import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.deploy.model.entity.HostModel;

public class DefaultProjectManager implements ProjectManager, Initializable {
	@Inject
	private DeploymentDao m_deploymentDao;

	@Inject
	private DeploymentDetailsDao m_deploymentDetailsDao;

	private Map<String, DeployModel> m_models = new HashMap<String, DeployModel>();

	@Override
	public Deployment findActiveDeploy(String type, String name) {
		try {
			Deployment deploy = m_deploymentDao.findActiveByWarTypeAndDomain(type, name, DeploymentEntity.READSET_FULL);

			return deploy;
		} catch (DalNotFoundException e) {
			return null;
		} catch (Exception e) {
			throw new RuntimeException(String.format("Error when finding active deployment by name(%s)!", name));
		}
	}

	@Override
	public DeployModel findModel(int deployId) {
		for (DeployModel model : m_models.values()) {
			if (model.getId() == deployId) {
				return model;
			}
		}

		try {
			Deployment d = m_deploymentDao.findByPK(deployId, DeploymentEntity.READSET_FULL);
			List<DeploymentDetails> detailsList = m_deploymentDetailsDao.findAllByDeployId(deployId,
					DeploymentDetailsEntity.READSET_FULL);
			DeployModel model = new DeployModel();
			DeployPlan plan = new DeployPlan();

			for (DeploymentDetails details : detailsList) {
				String rawLog = details.getRawLog();
				DeployModel deploy;

				if (rawLog != null) {
					deploy = com.dianping.phoenix.deploy.model.transform.DefaultSaxParser.parse(rawLog);
					for (HostModel host : deploy.getHosts().values()) {
						model.addHost(host);
					}
				} else {
					HostModel host = new HostModel();
					host.setId(details.getId());
					host.setIp(details.getIpAddress());
					model.addHost(host);
				}
			}

			plan.setAbortOnError(d.getErrorPolicy() == 1);
			plan.setPolicy(d.getStrategy());
			plan.setVersion(d.getWarVersion());
			plan.setSkipTest(d.getSkipTest() == 1);
			plan.setAutoContinue(d.getAutoContinue() == 1);
			plan.setDeployInterval(d.getDeployInterval());

			model.setId(deployId);
			model.setDomain(d.getDomain());
			model.setPlan(plan);
			model.setAbortOnError(plan.isAbortOnError());
			model.setSkipTest(plan.isSkipTest());
			model.setVersion(d.getWarVersion());
			model.setStatus(DeployStatus.getById(d.getStatus(), DeployStatus.UNKNOWN).getName());

			storeModel(model);
			return model;
		} catch (DalNotFoundException e) {
			// ignore it
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void initialize() throws InitializationException {
	}

	@Override
	public void storeModel(DeployModel model) {
		m_models.put(model.getDomain(), model);
	}

}

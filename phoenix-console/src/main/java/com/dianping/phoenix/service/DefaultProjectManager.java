package com.dianping.phoenix.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.unidal.dal.jdbc.DalNotFoundException;
import org.unidal.lookup.annotation.Inject;
import org.xml.sax.SAXException;

import com.dianping.phoenix.console.dal.deploy.Deployment;
import com.dianping.phoenix.console.dal.deploy.DeploymentDao;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetails;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsDao;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsEntity;
import com.dianping.phoenix.console.dal.deploy.DeploymentEntity;
import com.dianping.phoenix.deploy.DeployPlan;
import com.dianping.phoenix.deploy.model.entity.DeployModel;
import com.dianping.phoenix.deploy.model.entity.HostModel;
import com.dianping.phoenix.project.entity.BussinessLine;
import com.dianping.phoenix.project.entity.Project;
import com.dianping.phoenix.project.entity.Root;
import com.dianping.phoenix.project.transform.DefaultSaxParser;

public class DefaultProjectManager implements ProjectManager, Initializable, LogEnabled{
	@Inject
	private DeploymentDao m_deploymentDao;

	@Inject
	private DeploymentDetailsDao m_deploymentDetailsDao;
	
	@Inject
	private DeviceManager m_deviceManager;

	private Root m_root;
	
	private Logger m_logger;

	private Map<String, DeployModel> m_models = new HashMap<String, DeployModel>();


	@Override
	public void enableLogging(Logger logger) {
		this.m_logger = logger;
	}

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
				DeployModel deploy = com.dianping.phoenix.deploy.model.transform.DefaultSaxParser.parse(rawLog);

				for (HostModel host : deploy.getHosts().values()) {
					model.addHost(host);
				}
			}

			model.setId(deployId);
			model.setDomain(d.getDomain());
			model.setPlan(plan);

			plan.setAbortOnError(d.getErrorPolicy() == 1);
			plan.setPolicy(d.getStrategy());
			plan.setVersion(d.getWarVersion());
			plan.setSkipTest(d.getSkipTest() == 1);

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
		try {
			m_root = readConfigFromFile();
		} catch (Exception e) {
			throw new RuntimeException(
			      "Unable to load deploy model from resource(project.xml)!", e);
		}
	}
	
	private Root readConfigFromFile() throws SAXException, IOException{
		Root root = null;
		File file = new File("/data/appdatas/phoenix/project.xml");
		InputStream in = null;

		if (file.exists()) {
			in = new FileInputStream(file);
			System.out.println("read project.xml from /data/appdatas/phoenix/project.xml");
		}

		if (in == null) {
			// TODO test purpose
			in = getClass().getResourceAsStream("/com/dianping/phoenix/deploy/project.xml");
			System.out.println("read project.xml from classpath");
		}

		root = DefaultSaxParser.parse(in);
		
		return root;
	}

	@Override
	public List<BussinessLine> getBussinessLineList() throws Exception {
		
		Root root;
		try{
			root = readConfigFromFile();
		} catch(Exception e) {
			m_logger.warn("Fail to read project.xml",e);
			root = m_root;
		}
		List<BussinessLine> list = new ArrayList<BussinessLine>();

		list.addAll(root.getBussinessLines().values());
		return list;
	}
	

	@Override
	public void storeModel(DeployModel model) {
		m_models.put(model.getDomain(), model);
	}

	@Override
	public Project findProjectBy(String name) throws Exception {
		return m_deviceManager.findProjectBy(name);
	}

}

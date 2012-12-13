package com.dianping.phoenix.deploy;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.deploy.DeployExecutorTest.MockAgentListener;
import com.dianping.phoenix.deploy.DeployExecutorTest.MockConfigManager;
import com.dianping.phoenix.deploy.DeployExecutorTest.MockDeployListener;
import com.dianping.phoenix.deploy.event.AgentListener;
import com.dianping.phoenix.deploy.event.DeployListener;

public class DeployExecutorTestConfigurator extends AbstractResourceConfigurator {
	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new DeployExecutorTestConfigurator());
	}

	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(DeployListener.class, MockDeployListener.class));
		all.add(C(AgentListener.class, MockAgentListener.class));
		all.add(C(ConfigManager.class, MockConfigManager.class) //
		      .config(E("configFile").value("N/A")));

		return all;
	}

	@Override
	protected Class<?> getTestClass() {
		return DeployExecutorTest.class;
	}
}

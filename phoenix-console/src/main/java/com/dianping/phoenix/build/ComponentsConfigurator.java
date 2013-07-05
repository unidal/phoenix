package com.dianping.phoenix.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.dal.jdbc.datasource.JdbcDataSourceConfigurationManager;
import org.unidal.initialization.DefaultModuleManager;
import org.unidal.initialization.Module;
import org.unidal.initialization.ModuleManager;
import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.phoenix.PhoenixConsoleModule;
import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.console.dal.deploy.DeliverableDao;
import com.dianping.phoenix.console.dal.deploy.DeploymentDao;
import com.dianping.phoenix.console.dal.deploy.DeploymentDetailsDao;
import com.dianping.phoenix.deliverable.DefaultDeliverableManager;
import com.dianping.phoenix.deliverable.DeliverableManager;
import com.dianping.phoenix.deploy.DeployExecutor;
import com.dianping.phoenix.deploy.DeployListener;
import com.dianping.phoenix.deploy.DeployManager;
import com.dianping.phoenix.deploy.DeployPolicy;
import com.dianping.phoenix.deploy.agent.AgentListener;
import com.dianping.phoenix.deploy.internal.DefaultAgentListener;
import com.dianping.phoenix.deploy.internal.DefaultDeployExecutor;
import com.dianping.phoenix.deploy.internal.DefaultDeployListener;
import com.dianping.phoenix.deploy.internal.DefaultDeployManager;
import com.dianping.phoenix.service.DefaultGitService;
import com.dianping.phoenix.service.DefaultProjectManager;
import com.dianping.phoenix.service.DefaultWarService;
import com.dianping.phoenix.service.GitService;
import com.dianping.phoenix.service.LogService;
import com.dianping.phoenix.service.ProjectManager;
import com.dianping.phoenix.service.WarService;
import com.dianping.phoenix.service.cmdb.DefaultDeviceManager;
import com.dianping.phoenix.service.cmdb.DeviceManager;
import com.dianping.phoenix.service.netty.AgentStatusFetcher;
import com.dianping.phoenix.service.netty.DefaultAgentStatusFetcher;
import com.dianping.phoenix.service.resource.DefaultResourceManager;
import com.dianping.phoenix.service.resource.ResourceManager;

public class ComponentsConfigurator extends AbstractResourceConfigurator {
	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ComponentsConfigurator());
	}

	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(ConfigManager.class));

		defineServiceComponents(all);
		defineDatabaseComponents(all);
		defineWebComponents(all);

		return all;
	}

	private void defineDatabaseComponents(List<Component> all) {
		// setup datasource configuration manager
		all.add(C(JdbcDataSourceConfigurationManager.class) //
				.config(E("datasourceFile").value("/data/appdatas/phoenix/datasources.xml")));

		// Phoenix database
		all.addAll(new PhoenixDatabaseConfigurator().defineComponents());
	}

	private void defineServiceComponents(List<Component> all) {
		all.add(C(WarService.class, DefaultWarService.class) //
				.req(ConfigManager.class));
		all.add(C(GitService.class, DefaultGitService.class) //
				.req(ConfigManager.class, LogService.class));

		all.add(C(LogService.class));
		all.add(C(DeliverableManager.class, DefaultDeliverableManager.class) //
				.req(WarService.class, GitService.class, DeliverableDao.class, LogService.class));

		all.add(C(AgentStatusFetcher.class, DefaultAgentStatusFetcher.class).req(ConfigManager.class));
		all.add(C(DeviceManager.class, DefaultDeviceManager.class) //
				.req(ConfigManager.class));
		all.add(C(ProjectManager.class, DefaultProjectManager.class) //
				.req(DeploymentDao.class, DeploymentDetailsDao.class));
		all.add(C(ResourceManager.class, DefaultResourceManager.class)//
				.req(AgentStatusFetcher.class, DeviceManager.class));

		for (DeployPolicy policy : DeployPolicy.values()) {
			all.add(C(DeployExecutor.class, policy.getId(), DefaultDeployExecutor.class) //
					.req(ConfigManager.class, DeployListener.class, AgentListener.class) //
					.config(E("policy").value(policy.name())));
		}

		all.add(C(DeployListener.class, DefaultDeployListener.class) //
				.req(DeploymentDao.class, DeploymentDetailsDao.class, ProjectManager.class));
		all.add(C(AgentListener.class, DefaultAgentListener.class) //
				.req(DeploymentDetailsDao.class));
		all.add(C(DeployManager.class, DefaultDeployManager.class) //
				.req(ProjectManager.class, DeployListener.class));
	}
	private void defineWebComponents(List<Component> all) {
		all.add(C(Module.class, PhoenixConsoleModule.ID, PhoenixConsoleModule.class));
		all.add(C(ModuleManager.class, DefaultModuleManager.class) //
				.config(E("topLevelModules").value(PhoenixConsoleModule.ID)));

		// Please keep it as last
		all.addAll(new WebComponentConfigurator().defineComponents());
	}
}

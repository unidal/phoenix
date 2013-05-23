package com.dianping.maven.plugin.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.maven.plugin.phoenix.DefaultF5Manager;
import com.dianping.maven.plugin.phoenix.F5Manager;
import com.dianping.maven.plugin.phoenix.WorkspaceFacade;
import com.dianping.maven.plugin.tools.generator.dynamic.BizServerPropertiesGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.LaunchFileGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.RouterRuleGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.ServiceLionPropertiesGenerator;
import com.dianping.maven.plugin.tools.vcs.CodeRetrieverContext;
import com.dianping.maven.plugin.tools.vcs.CodeRetrieverManager;
import com.dianping.maven.plugin.tools.vcs.GitCodeRetriever;
import com.dianping.maven.plugin.tools.vcs.ICodeRetriever;
import com.dianping.maven.plugin.tools.vcs.SVNCodeRetriever;
import com.dianping.maven.plugin.tools.wms.DummyRepositoryManager;
import com.dianping.maven.plugin.tools.wms.RepositoryManager;
import com.dianping.maven.plugin.tools.wms.WorkspaceManagementService;
import com.dianping.maven.plugin.tools.wms.WorkspaceManagementServiceImpl;

public class ComponentsConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();
		
		all.add(C(CodeRetrieverManager.class));
		all.add(C(ICodeRetriever.class, CodeRetrieverContext.GIT, GitCodeRetriever.class));
		all.add(C(ICodeRetriever.class, CodeRetrieverContext.SVN, SVNCodeRetriever.class));

		all.add(C(RepositoryManager.class, DummyRepositoryManager.class));
		all.add(C(WorkspaceManagementService.class, WorkspaceManagementServiceImpl.class) //
				.req(RepositoryManager.class) //
				.req(CodeRetrieverManager.class));

		all.add(C(ServiceLionPropertiesGenerator.class));
		all.add(C(LaunchFileGenerator.class));
		all.add(C(BizServerPropertiesGenerator.class));
		all.add(C(RouterRuleGenerator.class));

		all.add(C(F5Manager.class, DefaultF5Manager.class));

		all.add(C(WorkspaceFacade.class) //
				.is(PER_LOOKUP) //
				.req(WorkspaceManagementService.class) //
				.req(ServiceLionPropertiesGenerator.class) //
				.req(LaunchFileGenerator.class) //
				.req(BizServerPropertiesGenerator.class) //
				.req(RouterRuleGenerator.class) //
				.req(F5Manager.class));

		return all;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ComponentsConfigurator());
	}
}

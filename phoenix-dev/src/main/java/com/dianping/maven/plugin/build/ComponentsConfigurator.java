package com.dianping.maven.plugin.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.maven.plugin.phoenix.DefaultF5Manager;
import com.dianping.maven.plugin.phoenix.F5Manager;
import com.dianping.maven.plugin.phoenix.WorkspaceFacade;
import com.dianping.maven.plugin.phoenix.model.visitor.BizServerContextVisitor;
import com.dianping.maven.plugin.phoenix.model.visitor.LaunchFileContextVisitor;
import com.dianping.maven.plugin.phoenix.model.visitor.RouterRuleContextVisitor;
import com.dianping.maven.plugin.phoenix.model.visitor.ServiceLionContextVisitor;
import com.dianping.maven.plugin.phoenix.model.visitor.WorkspaceContextVisitor;
import com.dianping.maven.plugin.tools.misc.file.LaunchFileGenerator;
import com.dianping.maven.plugin.tools.misc.file.ServiceLionPropertiesGenerator;
import com.dianping.maven.plugin.tools.wms.DummyRepositoryManager;
import com.dianping.maven.plugin.tools.wms.RepositoryManager;
import com.dianping.maven.plugin.tools.wms.WorkspaceManagementService;
import com.dianping.maven.plugin.tools.wms.WorkspaceManagementServiceImpl;

public class ComponentsConfigurator extends AbstractResourceConfigurator {
	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(RepositoryManager.class, DummyRepositoryManager.class));
		all.add(C(WorkspaceManagementService.class, WorkspaceManagementServiceImpl.class) //
				.req(RepositoryManager.class));
		
		all.add(C(ServiceLionPropertiesGenerator.class));
		all.add(C(LaunchFileGenerator.class));
		
		all.add(C(F5Manager.class, DefaultF5Manager.class));
		
		all.add(C(BizServerContextVisitor.class));
		all.add(C(LaunchFileContextVisitor.class));
		all.add(C(RouterRuleContextVisitor.class) //
				.req(F5Manager.class, "default", "f5Mgr") //
				.is(PER_LOOKUP));
		all.add(C(ServiceLionContextVisitor.class));
		all.add(C(WorkspaceContextVisitor.class));
		
		all.add(C(WorkspaceFacade.class) //
				.is(PER_LOOKUP) //
				.req(WorkspaceManagementService.class) //
				.req(ServiceLionPropertiesGenerator.class) //
				.req(LaunchFileGenerator.class) //
				.req(BizServerContextVisitor.class) //
				.req(LaunchFileContextVisitor.class) //
				.req(RouterRuleContextVisitor.class) //
				.req(ServiceLionContextVisitor.class) //
				.req(WorkspaceContextVisitor.class));

		return all;
	}

	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ComponentsConfigurator());
	}
}

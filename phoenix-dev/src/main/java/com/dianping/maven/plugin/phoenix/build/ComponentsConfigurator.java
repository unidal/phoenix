package com.dianping.maven.plugin.phoenix.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.maven.plugin.tools.vcs.CodeRetrieverContext;
import com.dianping.maven.plugin.tools.vcs.CodeRetrieverManager;
import com.dianping.maven.plugin.tools.vcs.GitCodeRetriever;
import com.dianping.maven.plugin.tools.vcs.ICodeRetriever;
import com.dianping.maven.plugin.tools.vcs.SVNCodeRetriever;
import com.dianping.maven.plugin.tools.wms.WorkspaceManagementService;
import com.dianping.maven.plugin.tools.wms.WorkspaceManagementServiceImpl;

class ComponentsConfigurator extends AbstractResourceConfigurator {
	public static void main(String[] args) {
		generatePlexusComponentsXmlFile(new ComponentsConfigurator());
	}

	@Override
	public List<Component> defineComponents() {
		List<Component> all = new ArrayList<Component>();

		all.add(C(CodeRetrieverManager.class));
		all.add(C(ICodeRetriever.class, CodeRetrieverContext.GIT, GitCodeRetriever.class));
		all.add(C(ICodeRetriever.class, CodeRetrieverContext.SVN, SVNCodeRetriever.class));

		all.add(C(WorkspaceManagementService.class, WorkspaceManagementServiceImpl.class) //
		      .req(CodeRetrieverManager.class));

		return all;
	}

}

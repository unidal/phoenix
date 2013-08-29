package com.dianping.maven.plugins.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.maven.plugins.web.UICreator;
import com.dianping.phoenix.dev.core.WorkspaceFacade;
import com.dianping.phoenix.dev.core.model.phoenix.entity.Phoenix;
import com.dianping.phoenix.dev.core.tools.console.ConsoleIO;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.DefaultF5Manager;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.F5Manager;
import com.dianping.phoenix.dev.core.tools.vcs.CodeRetrieverContext;
import com.dianping.phoenix.dev.core.tools.vcs.CodeRetrieverManager;
import com.dianping.phoenix.dev.core.tools.vcs.DefaultRepositoryServiceImpl;
import com.dianping.phoenix.dev.core.tools.vcs.GitCodeRetriever;
import com.dianping.phoenix.dev.core.tools.vcs.ICodeRetriever;
import com.dianping.phoenix.dev.core.tools.vcs.RepositoryService;
import com.dianping.phoenix.dev.core.tools.vcs.SVNCodeRetriever;
import com.dianping.phoenix.dev.core.tools.wms.DefaultRepositoryManager;
import com.dianping.phoenix.dev.core.tools.wms.PluginWorkspaceServiceImpl;
import com.dianping.phoenix.dev.core.tools.wms.RepositoryManager;
import com.dianping.phoenix.dev.core.tools.wms.WorkspaceService;
import com.dianping.phoenix.dev.core.tools.wms.AgentWorkspaceServiceImpl;

public class ComponentsConfigurator extends AbstractResourceConfigurator {
    @Override
    public List<Component> defineComponents() {
        List<Component> all = new ArrayList<Component>();

        all.add(C(CodeRetrieverManager.class));
        all.add(C(ICodeRetriever.class, CodeRetrieverContext.GIT, GitCodeRetriever.class));
        all.add(C(ICodeRetriever.class, CodeRetrieverContext.SVN, SVNCodeRetriever.class));

        all.add(C(Phoenix.class));

        all.add(C(RepositoryManager.class, DefaultRepositoryManager.class) //
                .req(Phoenix.class));
        all.add(C(RepositoryService.class, DefaultRepositoryServiceImpl.class)//
                .req(RepositoryManager.class) //
                .req(CodeRetrieverManager.class));
        
        all.add(C(WorkspaceService.class, "Plugin", PluginWorkspaceServiceImpl.class) //
                .req(RepositoryService.class) //
                .req(F5Manager.class) //
                .req(RepositoryManager.class));
        all.add(C(WorkspaceService.class, "Agent", AgentWorkspaceServiceImpl.class) //
                .req(RepositoryService.class) //
                .req(F5Manager.class) //
                .req(RepositoryManager.class));

        all.add(C(F5Manager.class, DefaultF5Manager.class));

        all.add(C(WorkspaceFacade.class) //
                .is(PER_LOOKUP)); //

        all.add(C(ConsoleIO.class));
        all.add(C(UICreator.class) //
                .req(WorkspaceFacade.class));

        return all;
    }

    public static void main(String[] args) {
        generatePlexusComponentsXmlFile(new ComponentsConfigurator());
    }
}

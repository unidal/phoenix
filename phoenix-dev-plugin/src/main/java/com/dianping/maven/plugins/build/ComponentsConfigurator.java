package com.dianping.maven.plugins.build;

import java.util.ArrayList;
import java.util.List;

import org.unidal.lookup.configuration.AbstractResourceConfigurator;
import org.unidal.lookup.configuration.Component;

import com.dianping.maven.plugins.web.UICreator;
import com.dianping.phoenix.dev.core.WorkspaceFacade;
import com.dianping.phoenix.dev.core.model.phoenix.entity.Phoenix;
import com.dianping.phoenix.dev.core.tools.console.ConsoleIO;
import com.dianping.phoenix.dev.core.tools.generator.BytemanScriptGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.BizServerPropertiesGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.DefaultF5Manager;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.F5Manager;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.LaunchFileGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.ServiceLionPropertiesGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.UrlRuleGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.WorkspaceStartSHGenerator;
import com.dianping.phoenix.dev.core.tools.vcs.CodeRetrieverContext;
import com.dianping.phoenix.dev.core.tools.vcs.CodeRetrieverManager;
import com.dianping.phoenix.dev.core.tools.vcs.DefaultRepositoryServiceImpl;
import com.dianping.phoenix.dev.core.tools.vcs.GitCodeRetriever;
import com.dianping.phoenix.dev.core.tools.vcs.ICodeRetriever;
import com.dianping.phoenix.dev.core.tools.vcs.RepositoryService;
import com.dianping.phoenix.dev.core.tools.vcs.SVNCodeRetriever;
import com.dianping.phoenix.dev.core.tools.wms.DefaultRepositoryManager;
import com.dianping.phoenix.dev.core.tools.wms.RepositoryManager;
import com.dianping.phoenix.dev.core.tools.wms.WorkspaceManagementService;
import com.dianping.phoenix.dev.core.tools.wms.WorkspaceManagementServiceImpl;

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
        all.add(C(WorkspaceManagementService.class, WorkspaceManagementServiceImpl.class) //
                .req(RepositoryService.class));

        all.add(C(ServiceLionPropertiesGenerator.class));
        all.add(C(LaunchFileGenerator.class));
        all.add(C(BizServerPropertiesGenerator.class));
        all.add(C(UrlRuleGenerator.class));

        all.add(C(F5Manager.class, DefaultF5Manager.class));
        all.add(C(BytemanScriptGenerator.class));
        all.add(C(WorkspaceStartSHGenerator.class));

        all.add(C(WorkspaceFacade.class) //
                .is(PER_LOOKUP) //
                .req(WorkspaceManagementService.class) //
                .req(ServiceLionPropertiesGenerator.class) //
                .req(LaunchFileGenerator.class) //
                .req(BizServerPropertiesGenerator.class) //
                .req(UrlRuleGenerator.class) //
                .req(F5Manager.class)//
                .req(RepositoryService.class)//
                .req(BytemanScriptGenerator.class) //
                .req(RepositoryManager.class)//
                .req(WorkspaceStartSHGenerator.class));

        all.add(C(ConsoleIO.class));
        all.add(C(UICreator.class) //
                .req(WorkspaceFacade.class));

        return all;
    }

    public static void main(String[] args) {
        generatePlexusComponentsXmlFile(new ComponentsConfigurator());
    }
}

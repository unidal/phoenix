package com.dianping.phoenix.dev.core.tools.generator.dynamic.model.visitor;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.dianping.phoenix.dev.core.model.workspace.entity.BizProject;
import com.dianping.phoenix.dev.core.model.workspace.entity.Workspace;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.ServiceLionContext;

public class ServiceLionContextVisitor extends AbstractVisitor<ServiceLionContext> {

    public ServiceLionContextVisitor() {
        result = new ServiceLionContext();
        result.setServiceHost("127.0.0.1");
    }

    @Override
    public void visitWorkspace(Workspace workspace) {
        Map<String, File> projectBaseDirMapping = new HashMap<String, File>();
        for (BizProject bizProject : workspace.getBizProjects()) {
            projectBaseDirMapping.put(bizProject.getName(), new File(workspace.getDir(), bizProject.getName()));
        }
        result.setProjectBaseDirMapping(projectBaseDirMapping);
        result.setServiceMetaConfig(new File(workspace.getDir(),
                "phoenix/config/service-meta.xml"));
        super.visitWorkspace(workspace);
    }

}

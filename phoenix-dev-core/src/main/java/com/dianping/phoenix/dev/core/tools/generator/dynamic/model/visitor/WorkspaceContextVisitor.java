package com.dianping.phoenix.dev.core.tools.generator.dynamic.model.visitor;

import java.io.File;

import com.dianping.phoenix.dev.core.model.workspace.entity.BizProject;
import com.dianping.phoenix.dev.core.model.workspace.entity.PhoenixProject;
import com.dianping.phoenix.dev.core.model.workspace.entity.Workspace;
import com.dianping.phoenix.dev.core.tools.wms.WorkspaceContext;

public class WorkspaceContextVisitor extends AbstractVisitor<WorkspaceContext> {

    public WorkspaceContextVisitor() {
        result = new WorkspaceContext();
    }

    @Override
    public void visitBizProject(BizProject bizProject) {
        result.addProject(bizProject);
        super.visitBizProject(bizProject);
    }

    @Override
    public void visitPhoenixProject(PhoenixProject phoenixProject) {
        result.setPhoenixRouterVersion(phoenixProject.getRouter().getVersion());
        super.visitPhoenixProject(phoenixProject);
    }

    @Override
    public void visitWorkspace(Workspace workspace) {
        result.setBaseDir(new File(workspace.getDir()));
        super.visitWorkspace(workspace);
    }

}

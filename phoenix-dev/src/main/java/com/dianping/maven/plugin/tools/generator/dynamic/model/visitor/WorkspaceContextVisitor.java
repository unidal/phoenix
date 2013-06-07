package com.dianping.maven.plugin.tools.generator.dynamic.model.visitor;

import java.io.File;

import com.dianping.maven.plugin.phoenix.model.entity.BizProject;
import com.dianping.maven.plugin.phoenix.model.entity.PhoenixProject;
import com.dianping.maven.plugin.phoenix.model.entity.Workspace;
import com.dianping.maven.plugin.tools.wms.WorkspaceContext;

public class WorkspaceContextVisitor extends AbstractVisitor<WorkspaceContext> {

    public WorkspaceContextVisitor() {
        result = new WorkspaceContext();
    }

    @Override
    public void visitBizProject(BizProject bizProject) {
        result.addProject(bizProject.getName());
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

package com.dianping.maven.plugin.tools.generator.dynamic.model.visitor;

import java.io.File;

import com.dianping.maven.plugin.phoenix.model.entity.Workspace;
import com.dianping.maven.plugin.tools.generator.dynamic.LaunchFileContext;
import com.dianping.maven.plugin.tools.wms.WorkspaceConstants;

public class LaunchFileContextVisitor extends AbstractVisitor<LaunchFileContext> {

	public LaunchFileContextVisitor() {
		result = new LaunchFileContext();
		result.setMainClass("com.dianping.phoenix.container.PhoenixServer");
	}

	@Override
	public void visitWorkspace(Workspace workspace) {
		result.setBtmFile(new File(new File(workspace.getDir()), WorkspaceConstants.PHOENIX_META_FOLDER + "service-lion.btm"));
		super.visitWorkspace(workspace);
	}

}

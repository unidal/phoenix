package com.dianping.phoenix.dev.core.tools.generator.dynamic.model.visitor;

import java.io.File;

import com.dianping.phoenix.dev.core.model.workspace.entity.Workspace;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.LaunchFileContext;
import com.dianping.phoenix.dev.core.tools.wms.WorkspaceConstants;

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

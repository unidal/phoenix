package com.dianping.maven.plugin.tools.generator.dynamic.model.visitor;

import java.io.File;

import com.dianping.maven.plugin.phoenix.model.entity.Workspace;
import com.dianping.maven.plugin.tools.generator.dynamic.LaunchFileContext;

public class LaunchFileContextVisitor extends AbstractVisitor<LaunchFileContext> {

	public LaunchFileContextVisitor() {
		result = new LaunchFileContext();
		result.setMainClass("com.dianping.phoenix.container.PhoenixServer");
	}

	@Override
	public void visitWorkspace(Workspace workspace) {
		result.setBtmFile(new File(new File(workspace.getDir()), "phoenix-container/src/main/resources/service-lion.btm"));
		super.visitWorkspace(workspace);
	}

}

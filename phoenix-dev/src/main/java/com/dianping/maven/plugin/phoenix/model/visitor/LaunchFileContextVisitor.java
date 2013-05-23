package com.dianping.maven.plugin.phoenix.model.visitor;

import java.io.File;

import com.dianping.maven.plugin.phoenix.model.entity.Workspace;
import com.dianping.maven.plugin.tools.generator.dynamic.LaunchFileContext;

public class LaunchFileContextVisitor extends AbstractVisitor<LaunchFileContext> {

	public LaunchFileContextVisitor() {
		result = new LaunchFileContext();
		result.setMainClass("com.dianping.phoenix.container.BizServer");
	}

	@Override
	public void visitWorkspace(Workspace workspace) {
		result.setBtmFile(new File(workspace.getDir() + "/src/main/resources/phoenix.launch"));
		super.visitWorkspace(workspace);
	}

}

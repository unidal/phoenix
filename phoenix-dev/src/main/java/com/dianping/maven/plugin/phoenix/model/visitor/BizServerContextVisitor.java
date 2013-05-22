package com.dianping.maven.plugin.phoenix.model.visitor;

import java.io.File;

import com.dianping.maven.plugin.phoenix.BizServerContext;
import com.dianping.maven.plugin.phoenix.model.entity.BizProject;

public class BizServerContextVisitor extends AbstractVisitor<BizServerContext> {

	private File wsDir;
	
	public BizServerContextVisitor() {
		result = new BizServerContext();
	}

	@Override
	public void visitBizProject(BizProject bizProject) {
		String projectName = bizProject.getName();
		result.addWebContext("_" + projectName, new File(wsDir, projectName));
		super.visitBizProject(bizProject);
	}

}

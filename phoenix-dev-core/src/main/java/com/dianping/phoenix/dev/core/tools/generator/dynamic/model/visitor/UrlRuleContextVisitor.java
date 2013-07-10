package com.dianping.phoenix.dev.core.tools.generator.dynamic.model.visitor;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.dianping.phoenix.dev.core.model.workspace.entity.BizProject;
import com.dianping.phoenix.dev.core.model.workspace.entity.VirtualServer;
import com.dianping.phoenix.dev.core.model.workspace.entity.Workspace;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.F5Manager;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.F5Pool;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.UrlRuleContext;
import com.dianping.phoenix.dev.core.tools.utils.PomParser;
import com.dianping.phoenix.dev.core.tools.utils.WebProjectFileFilter;

public class UrlRuleContextVisitor extends AbstractVisitor<UrlRuleContext> {

	private F5Manager f5Mgr;
	private File wsDir;

	public UrlRuleContextVisitor(F5Manager f5Mgr) {
		result = new UrlRuleContext();
		this.f5Mgr = f5Mgr;
	}

	@Override
	public void visitBizProject(BizProject bizProject) {
		File parentProjectDir = new File(wsDir, bizProject.getName());
		
		FileFilter webFilter = new WebProjectFileFilter();
		List<File> webProjects = new ArrayList<File>(Arrays.asList(parentProjectDir.listFiles(webFilter)));
		if(webFilter.accept(parentProjectDir)) {
			webProjects.add(parentProjectDir);
		}

		PomParser pomParser = new PomParser();
		for (File webProject : webProjects) {
			F5Pool pool = f5Mgr.poolForProject(pomParser.getArtifactId(webProject));
			if (pool != null) {
				// web project
				result.addLocalPool(pool);
			}
		}

		super.visitBizProject(bizProject);
	}

	@Override
	public void visitVirtualServer(VirtualServer virtualServer) {
		result.addVirtualServer(virtualServer);
		super.visitVirtualServer(virtualServer);
	}

	@Override
	public void visitWorkspace(Workspace ws) {
		wsDir = new File(ws.getDir());
		super.visitWorkspace(ws);
	}

}

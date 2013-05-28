package com.dianping.maven.plugin.phoenix.model.visitor;

import java.io.File;
import java.io.FileFilter;

import org.apache.log4j.Logger;

import com.dianping.maven.plugin.phoenix.BizServerContext;
import com.dianping.maven.plugin.phoenix.model.entity.BizProject;
import com.dianping.maven.plugin.phoenix.model.entity.Workspace;

public class BizServerContextVisitor extends AbstractVisitor<BizServerContext> {

	private static Logger log = Logger.getLogger(BizServerContextVisitor.class);

	private File wsDir;

	public BizServerContextVisitor() {
		result = new BizServerContext();
	}

	private boolean hasWebProjectIn(File dir) {
		return new File(dir, "src/main/webapp/WEB-INF/web.xml").exists();
	}

	@Override
	public void visitBizProject(BizProject bizProject) {
		String projectName = bizProject.getName();
		File projectDir = new File(wsDir, projectName);
		
		File webProjectDirFound = null;
		if (hasWebProjectIn(projectDir)) {
			// top level is web project
			webProjectDirFound = projectDir;
		} else {
			// second level is web project
			File[] webProjectSubDirs = projectDir.listFiles(new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					return pathname.isDirectory() && hasWebProjectIn(pathname);
				}
			});
			if(webProjectSubDirs.length == 0) {
				log.warn(String.format("no web project found in %s", projectDir.getAbsolutePath()));
			} else if(webProjectSubDirs.length == 1) {
				webProjectDirFound = webProjectSubDirs[0];
			} else {
				log.warn(String.format("more than one web project found in %s", projectDir.getAbsolutePath()));
			}
		}
		
		if (webProjectDirFound != null) {
			result.addWebContext("/_" + projectName, webProjectDirFound);
		}
		super.visitBizProject(bizProject);
	}

	@Override
	public void visitWorkspace(Workspace workspace) {
		wsDir = new File(workspace.getDir());
		super.visitWorkspace(workspace);
	}

}

package com.dianping.phoenix.dev.core.tools.generator.dynamic.model.visitor;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.dianping.phoenix.dev.core.model.workspace.entity.BizProject;
import com.dianping.phoenix.dev.core.model.workspace.entity.Workspace;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.BizServerContext;
import com.dianping.phoenix.dev.core.tools.utils.PomParser;
import com.dianping.phoenix.dev.core.tools.utils.WebProjectFileFilter;

public class BizServerContextVisitor extends AbstractVisitor<BizServerContext> {

	private static Logger log = Logger.getLogger(BizServerContextVisitor.class);

	private File wsDir;

	public BizServerContextVisitor() {
		result = new BizServerContext();
	}

	@Override
	public void visitBizProject(BizProject bizProject) {
		File parentProjectDir = new File(wsDir, bizProject.getName());

		FileFilter webFilter = new WebProjectFileFilter();
		List<File> webProjectSubDirs = new ArrayList<File>(Arrays.asList(parentProjectDir.listFiles(webFilter)));
		if(webFilter.accept(parentProjectDir)) {
			webProjectSubDirs.add(parentProjectDir);
		}

		if (webProjectSubDirs != null) {
			for (File webProjectDir : webProjectSubDirs) {
				String projectName = parseProjectName(webProjectDir);
				log.info(String.format("found web project in %s", webProjectDir.getAbsolutePath()));
				result.addWebContext("/_" + projectName, propertiesEscape(webProjectDir.getAbsolutePath()));
			}
		}

		super.visitBizProject(bizProject);
	}

	/**
	 * pom.xml have the "most correct" name, use directory name for simplicity
	 * 
	 * @param file
	 * @return
	 */
	private String parseProjectName(File file) {
		return new PomParser().getArtifactId(file);
	}

	private String propertiesEscape(String propertyValue) {
		return propertyValue.replaceAll("\\\\", "/");
	}

	@Override
	public void visitWorkspace(Workspace workspace) {
		wsDir = new File(workspace.getDir());
		super.visitWorkspace(workspace);
	}

}

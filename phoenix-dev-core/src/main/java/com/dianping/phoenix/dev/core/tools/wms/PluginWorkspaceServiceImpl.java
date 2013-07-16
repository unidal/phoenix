package com.dianping.phoenix.dev.core.tools.wms;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;

import com.dianping.phoenix.dev.core.model.workspace.entity.BizProject;
import com.dianping.phoenix.dev.core.model.workspace.entity.Workspace;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.ContainerBizServerGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.ContainerPomXMLGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.ContainerWebXMLGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.LaunchFileContext;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.LaunchFileGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.WorkspaceEclipseBatGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.WorkspaceEclipseSHGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.WorkspacePomXMLGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.model.visitor.LaunchFileContextVisitor;
import com.dianping.phoenix.dev.core.tools.remedy.PomRemedy;

public class PluginWorkspaceServiceImpl extends AbstractWorkspaceService {
	
	@Override
	protected void generateContainer(WorkspaceContext context, OutputStream out) throws Exception {
		
		checkoutSource(context, out);
		
		File projectBase = new File(context.getBaseDir(), WorkspaceConstants.PHOENIX_CONTAINER_FOLDER);
		File sourceFolder = new File(projectBase, "src/main/java");
		File resourceFolder = new File(projectBase, "src/main/resources");
		File webinfFolder = new File(projectBase, "src/main/webapp/WEB-INF");
		FileUtils.forceMkdir(sourceFolder);
		FileUtils.forceMkdir(resourceFolder);
		FileUtils.forceMkdir(webinfFolder);

		copyFile("log4j.xml", resourceFolder);

		// web.xml
		ContainerWebXMLGenerator containerWebXMLGenerator = new ContainerWebXMLGenerator();
		containerWebXMLGenerator.generate(new File(webinfFolder, "web.xml"), null);

		// pom.xml
		ContainerPomXMLGenerator containerPomXMLGenerator = new ContainerPomXMLGenerator();
		Map<String, String> containerPomXMLGeneratorContext = new HashMap<String, String>();
		containerPomXMLGeneratorContext.put("phoenixRouterVersion", context.getPhoenixRouterVersion());
		containerPomXMLGenerator.generate(new File(projectBase, "pom.xml"), containerPomXMLGeneratorContext);

		// BizServer.java
		if (WorkspaceConstants.FROM_PLUGIN.equalsIgnoreCase(context.getFrom())) {
			ContainerBizServerGenerator containerBizServerGenerator = new ContainerBizServerGenerator();
			containerBizServerGenerator.generate(new File(sourceFolder,
					"com/dianping/phoenix/container/PhoenixServer.java"), null);
		}
	}

	@Override
	protected void generateWorkspaceMisc(WorkspaceContext context) throws Exception {
		// pom.xml
		List<String> projectNames = new ArrayList<String>();
		for (BizProject project : context.getProjects()) {
			projectNames.add(project.getName());
		}
		WorkspacePomXMLGenerator generator = new WorkspacePomXMLGenerator();
		generator.generate(new File(context.getBaseDir(), "pom.xml"), projectNames);

		// eclipse.sh/bat
		if (SystemUtils.IS_OS_WINDOWS) {
			WorkspaceEclipseBatGenerator workspaceEclipseBatGenerator = new WorkspaceEclipseBatGenerator();
			File eclipseBatFile = new File(context.getBaseDir(), "eclipse.bat");
			workspaceEclipseBatGenerator.generate(eclipseBatFile, null);
			eclipseBatFile.setExecutable(true);
		} else {
			WorkspaceEclipseSHGenerator workspaceEclipseSHGenerator = new WorkspaceEclipseSHGenerator();
			File eclipseSHFile = new File(context.getBaseDir(), "eclipse.sh");
			workspaceEclipseSHGenerator.generate(eclipseSHFile, null);
			eclipseSHFile.setExecutable(true);
		}
		
		PomRemedy.INSTANCE.remedyPomIn(context.getBaseDir());
	}

	@Override
	protected File resourceFileFor(File rootDir, String fileName) {
		return new File(rootDir, WorkspaceConstants.PHOENIX_RESOURCE_FOLDER + fileName);
	}

	@Override
	protected void generateRuntimeResources(Workspace model) throws Exception {
		super.generateRuntimeResources(model);

		File projectDir = new File(model.getDir());
		LaunchFileContextVisitor launchFileContextVisitor = new LaunchFileContextVisitor();
		model.accept(launchFileContextVisitor);
		createEcliseLaunchFile(rootFileFor(projectDir, "phoenix.launch"), launchFileContextVisitor.getVisitResult());
	}

	void createEcliseLaunchFile(File launchFile, LaunchFileContext ctx) throws Exception {
		new LaunchFileGenerator().generate(launchFile, ctx);
	}

	private void checkoutSource(WorkspaceContext context, OutputStream out) throws Exception {
		for (BizProject project : context.getProjects()) {
			repositoryService.checkout(project.getName(), new File(context.getBaseDir(), project.getName()), out);
		}
	}

	@Override
	public void create(Workspace model, OutputStream out) throws Exception {
		super.create(model, out);
		
		// create ws dir
		File workspaceFolder = new File(model.getDir(), "ws");
		if (!workspaceFolder.exists()) {
			FileUtils.forceMkdir(workspaceFolder);
		}
	}
	
}

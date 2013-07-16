package com.dianping.phoenix.dev.core.tools.wms;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.dev.core.model.workspace.entity.Workspace;
import com.dianping.phoenix.dev.core.tools.generator.BytemanScriptGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.BizServerContext;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.BizServerPropertiesGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.F5Manager;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.ServiceLionContext;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.ServiceLionPropertiesGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.UrlRuleContext;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.UrlRuleGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.model.visitor.BizServerContextVisitor;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.model.visitor.ServiceLionContextVisitor;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.model.visitor.UrlRuleContextVisitor;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.model.visitor.WorkspaceContextVisitor;
import com.dianping.phoenix.dev.core.tools.vcs.RepositoryService;

public abstract class AbstractWorkspaceService implements WorkspaceService {

	final static String LINE_SEPARATOR = System.getProperty("line.separator");
	private static Logger log = Logger.getLogger(AbstractWorkspaceService.class);
	
	@Inject
	protected F5Manager f5Mgr;
	@Inject
	protected RepositoryService repositoryService;
	@Inject
	protected RepositoryManager repoMgr;

	protected void copyFile(String fileName, File resourceFolder) throws FileNotFoundException, IOException {
		InputStream source = this.getClass().getResourceAsStream("/" + fileName);
		FileOutputStream dest = new FileOutputStream(new File(resourceFolder, fileName));
		IOUtils.copy(source, dest);
		IOUtils.closeQuietly(source);
		IOUtils.closeQuietly(dest);
	}

	protected void copyGitFileToClasspath(Workspace model) throws IOException {
		File wsDir = new File(model.getDir());
		String[] filesToCopy = new String[]{"url-rules-main.vm", "url-rules-tuangou.vm", "virtualServer.properties"};
		
		for (int i = 0; i < filesToCopy.length; i++) {
			File srcFile = new File(new File(wsDir, WorkspaceConstants.PHOENIX_CONFIG_FOLDER), filesToCopy[i]);
			File destDir = resourceFileFor(wsDir, "");
			FileUtils.copyFileToDirectory(srcFile, destDir);
		}
	}

	@Override
	public void create(Workspace model, OutputStream out) throws Exception {
		WorkspaceContext context = extractWorkspaceModel(model);

		if (context.getProjects() != null && context.getBaseDir() != null && out != null) {

			printContent("Generating phoenix workspace...", out);
			ensureWorkspaceDirExisted(out, context);

			printContent("Generating phoenix container...", out);
			generateContainer(context, out);

			printContent("Generating phoenix workspace misc files...", out);
			generateWorkspaceMisc(context);

			printContent("Generating runtime resources...", out);
			generateRuntimeResources(model);

			printContent("All done. Cheers~", out);

		} else {
			throw new Exception("projects/basedir can not be null");
		}
	}

	private void ensureWorkspaceDirExisted(OutputStream out, WorkspaceContext context) throws IOException {
		if (!context.getBaseDir().exists()) {
			FileUtils.forceMkdir(context.getBaseDir());
			printContent(String.format("Workspace folder(%s) created...", context.getBaseDir()), out);
		}
	}

	protected void createBizServerProperties(File bizServerFile, BizServerContext ctx) throws IOException {
		new BizServerPropertiesGenerator().generate(bizServerFile, ctx);
	}

	protected void createBytemanFile(File bytemanFile) throws Exception {
		new BytemanScriptGenerator().generate(bytemanFile, new HashMap<String, String>());
	}

	protected void createLionProperties(File lionFile, ServiceLionContext ctx) throws Exception {
		new ServiceLionPropertiesGenerator().generate(lionFile, ctx);
	}

	protected void generateRuntimeResources(Workspace model) throws Exception {
		File projectDir = new File(model.getDir());
		
		copyGitFileToClasspath(model);
		generateLib(model);

		UrlRuleContextVisitor routerRuleCtxVisitor = new UrlRuleContextVisitor(f5Mgr);
		BizServerContextVisitor bizServerCtxVisitor = new BizServerContextVisitor();
		ServiceLionContextVisitor serviceLionCtxVisitor = new ServiceLionContextVisitor();

		model.accept(routerRuleCtxVisitor);
		model.accept(bizServerCtxVisitor);
		model.accept(serviceLionCtxVisitor);

		createUrlRuleXml(resourceFileFor(projectDir, ""), routerRuleCtxVisitor.getVisitResult());
		createBizServerProperties(resourceFileFor(projectDir, "phoenix.xml"), bizServerCtxVisitor.getVisitResult());
		createLionProperties(resourceFileFor(projectDir, "router-service.xml"), serviceLionCtxVisitor.getVisitResult());
		createBytemanFile(metaFileFor(projectDir, "service-lion.btm"));
		
	}

	protected void createUrlRuleXml(File resourceDir, UrlRuleContext ctx) throws IOException {
		new UrlRuleGenerator().generate(resourceDir, ctx);
	}

	private WorkspaceContext extractWorkspaceModel(Workspace model) {
		WorkspaceContextVisitor wsVisitor = new WorkspaceContextVisitor();
		model.accept(wsVisitor);
		WorkspaceContext context = wsVisitor.getVisitResult();
		return context;
	}

	protected abstract void generateContainer(WorkspaceContext context, OutputStream out) throws Exception;

	private void generateLib(Workspace model) throws Exception {
		File libFolder = new File(model.getDir(), WorkspaceConstants.PHOENIX_LIB_FOLDER);

		FileUtils.forceMkdir(libFolder);
		copyFile("byteman-2.1.2.jar", libFolder);
		copyFile("instrumentation-util-0.0.1.jar", libFolder);
	}

	protected abstract void generateWorkspaceMisc(WorkspaceContext context) throws Exception;
	
	public List<String> getProjectListByPattern(String pattern) {
		return repoMgr.getProjectListByPattern(pattern);
	}

	protected File metaFileFor(File rootDir, String fileName) {
		return new File(rootDir, WorkspaceConstants.PHOENIX_META_FOLDER + fileName);
	}

	@Override
	public void modify(Workspace model, OutputStream out) throws Exception {
		create(model, out);
	}
	
	protected void printContent(String content, OutputStream out) {

		try {
			out.write(("[INFO] ------------------------------------------------------------------------" + LINE_SEPARATOR)
					.getBytes());
			out.write(("[INFO] " + content + LINE_SEPARATOR).getBytes());
			out.write(("[INFO] ------------------------------------------------------------------------" + LINE_SEPARATOR)
					.getBytes());
		} catch (IOException e) {
			// ignore
		}
	}
	
	public void pullConfig(File wsDir) {
		File configFolder = new File(wsDir, WorkspaceConstants.PHOENIX_CONFIG_FOLDER);
		if (configFolder.exists()) {
			FileUtils.deleteQuietly(configFolder);
		}

		log.debug("try to update phoenix config from remote git repository");
		try {
			repositoryService.checkout("phoenix-maven-config", configFolder, System.out);
		} catch (Exception e) {
			log.warn("error update phoenix config from remote git repository, plugin will use config file embedded.", e);
		}
	}
	
	abstract protected File resourceFileFor(File rootDir, String fileName);
	
	protected File rootFileFor(File rootDir, String fileName) {
		return new File(rootDir, WorkspaceConstants.PHOENIX_CONTAINER_FOLDER + fileName);
	}

}

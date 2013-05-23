package com.dianping.maven.plugin.phoenix;

import java.io.File;
import java.io.IOException;

import org.unidal.lookup.annotation.Inject;

import com.dianping.maven.plugin.phoenix.model.entity.Workspace;
import com.dianping.maven.plugin.phoenix.model.visitor.BizServerContextVisitor;
import com.dianping.maven.plugin.phoenix.model.visitor.LaunchFileContextVisitor;
import com.dianping.maven.plugin.phoenix.model.visitor.RouterRuleContextVisitor;
import com.dianping.maven.plugin.phoenix.model.visitor.ServiceLionContextVisitor;
import com.dianping.maven.plugin.phoenix.model.visitor.WorkspaceContextVisitor;
import com.dianping.maven.plugin.tools.misc.file.LaunchFileContext;
import com.dianping.maven.plugin.tools.misc.file.LaunchFileGenerator;
import com.dianping.maven.plugin.tools.misc.file.ServiceLionContext;
import com.dianping.maven.plugin.tools.misc.file.ServiceLionPropertiesGenerator;
import com.dianping.maven.plugin.tools.velocity.VelocityEngineManager;
import com.dianping.maven.plugin.tools.wms.WorkspaceContext;
import com.dianping.maven.plugin.tools.wms.WorkspaceManagementException;
import com.dianping.maven.plugin.tools.wms.WorkspaceManagementService;

public class WorkspaceFacade {

	@Inject
	private WorkspaceManagementService wms;
	@Inject
	private ServiceLionPropertiesGenerator lionGenerator;
	@Inject
	private LaunchFileGenerator launchGenerator;
	@Inject
	private F5Manager f5Mgr;

	public void create(Workspace model) throws Exception {
		WorkspaceContextVisitor workspaceCtxVisitor = new WorkspaceContextVisitor();
		model.accept(workspaceCtxVisitor);
		createSkeletonWorkspace(workspaceCtxVisitor.getVisitResult());
		createResources(model);
	}

	private File resourceFileFor(File rootDir, String fileName) {
		return new File(rootDir, "phoenix-container/src/main/resources/" + fileName);
	}

	void createResources(Workspace model) throws Exception {
		File projectDir = new File(model.getDir());

		RouterRuleContextVisitor routerRuleCtxVisitor = new RouterRuleContextVisitor(f5Mgr);
		BizServerContextVisitor bizServerCtxVisitor = new BizServerContextVisitor();
		ServiceLionContextVisitor serviceLionCtxVisitor = new ServiceLionContextVisitor();
		LaunchFileContextVisitor launchFileContextVisitor = new LaunchFileContextVisitor();

		model.accept(routerRuleCtxVisitor);
		model.accept(bizServerCtxVisitor);
		model.accept(serviceLionCtxVisitor);
		model.accept(launchFileContextVisitor);

		createRouterRuleXml(resourceFileFor(projectDir, "router-rules.xml"), routerRuleCtxVisitor.getVisitResult());
		createBizServerProperties(resourceFileFor(projectDir, "bizServer.properties"),
				bizServerCtxVisitor.getVisitResult());
		createLionProperties(resourceFileFor(projectDir, "service-lion.btm"), serviceLionCtxVisitor.getVisitResult());
		createEcliseLaunchFile(resourceFileFor(projectDir, "phoenix.launch"), launchFileContextVisitor.getVisitResult());
	}

	File createSkeletonWorkspace(WorkspaceContext wsCtx) throws WorkspaceManagementException {
		return wms.create(wsCtx, System.out);
	}

	void createRouterRuleXml(File routerRulesFile, RouterRuleContext ctx) throws IOException {
		VelocityEngineManager.INSTANCE.build("/router-rules.vm", ctx, routerRulesFile);
	}

	void createBizServerProperties(File bizServerFile, BizServerContext ctx) throws IOException {
		VelocityEngineManager.INSTANCE.build("/bizServer.vm", ctx, bizServerFile);
	}

	void createLionProperties(File lionFile, ServiceLionContext ctx) throws Exception {
		lionGenerator.generate(lionFile, ctx);
	}

	void createEcliseLaunchFile(File launchFile, LaunchFileContext ctx) throws Exception {
		launchGenerator.generate(launchFile, ctx);
	}

}

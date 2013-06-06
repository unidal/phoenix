package com.dianping.maven.plugin.phoenix;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.unidal.lookup.annotation.Inject;

import com.dianping.maven.plugin.phoenix.model.entity.Workspace;
import com.dianping.maven.plugin.phoenix.model.transform.DefaultSaxParser;
import com.dianping.maven.plugin.tools.generator.BytemanScriptGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.BizServerContext;
import com.dianping.maven.plugin.tools.generator.dynamic.BizServerPropertiesGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.F5Manager;
import com.dianping.maven.plugin.tools.generator.dynamic.LaunchFileContext;
import com.dianping.maven.plugin.tools.generator.dynamic.LaunchFileGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.RouterRuleContext;
import com.dianping.maven.plugin.tools.generator.dynamic.RouterRuleGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.ServiceLionContext;
import com.dianping.maven.plugin.tools.generator.dynamic.ServiceLionPropertiesGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.model.visitor.BizServerContextVisitor;
import com.dianping.maven.plugin.tools.generator.dynamic.model.visitor.LaunchFileContextVisitor;
import com.dianping.maven.plugin.tools.generator.dynamic.model.visitor.RouterRuleContextVisitor;
import com.dianping.maven.plugin.tools.generator.dynamic.model.visitor.ServiceLionContextVisitor;
import com.dianping.maven.plugin.tools.generator.dynamic.model.visitor.WorkspaceContextVisitor;
import com.dianping.maven.plugin.tools.vcs.RepositoryService;
import com.dianping.maven.plugin.tools.wms.WorkspaceContext;
import com.dianping.maven.plugin.tools.wms.WorkspaceManagementException;
import com.dianping.maven.plugin.tools.wms.WorkspaceManagementService;

public class WorkspaceFacade {

    @Inject
    private WorkspaceManagementService     wms;
    @Inject
    private ServiceLionPropertiesGenerator lionGenerator;
    @Inject
    private LaunchFileGenerator            launchGenerator;
    @Inject
    private BizServerPropertiesGenerator   bizGenerator;
    @Inject
    private RouterRuleGenerator            routerGenerator;
    @Inject
    private F5Manager                      f5Mgr;
    @Inject
    private RepositoryService              repositoryService;
    @Inject
    private BytemanScriptGenerator         bytemanGenerator;

    private static final String            PHOENIX_ROOT_FOLDER     = "phoenix/";
    private static final String            WORKSPACE_META_FILENAME = PHOENIX_ROOT_FOLDER + "meta/phoenix.meta";
    private static final String            REINIT_SIG_FILENAME     = PHOENIX_ROOT_FOLDER + "meta/phoenix.new";

    public void create(Workspace model) throws Exception {
        workspaceChange(model, false);
    }

    public Workspace current(File dir) throws Exception {
        File metaFile = new File(dir, WORKSPACE_META_FILENAME);
        if (metaFile.exists() && metaFile.isFile()) {
            return DefaultSaxParser.parse(FileUtils.readFileToString(metaFile));
        }
        return null;
    }

    public void modify(Workspace model) throws Exception {
        workspaceChange(model, true);
    }

    private void workspaceChange(Workspace model, boolean modify) throws Exception {
        WorkspaceContextVisitor workspaceCtxVisitor = new WorkspaceContextVisitor();
        model.accept(workspaceCtxVisitor);

        FileUtils.forceMkdir(new File(model.getDir(), PHOENIX_ROOT_FOLDER));

        pullConfig(model);

        if (modify) {
            modifySkeletonWorkspace(workspaceCtxVisitor.getVisitResult());
        } else {
            createSkeletonWorkspace(workspaceCtxVisitor.getVisitResult());
        }
        createRuntimeResources(model);
        saveMeta(model);
        FileUtils.touch(new File(model.getDir(), REINIT_SIG_FILENAME));
    }

    private void pullConfig(Workspace model) {
        File configFolder = new File(model.getDir(), PHOENIX_ROOT_FOLDER + "config");
        if (configFolder.exists()) {
            FileUtils.deleteQuietly(configFolder);
        }
        try {
            repositoryService.checkout("phoenix-maven-config", configFolder, System.out);
        } catch (Exception e) {
            System.out.println("Pull phoenix-maven-config failed, plugin will use config file embedded.");
        }
    }

    private void saveMeta(Workspace model) throws Exception {
        FileUtils.writeStringToFile(new File(model.getDir(), WORKSPACE_META_FILENAME), model.toString(), "utf-8");
    }

    private File resourceFileFor(File rootDir, String fileName) {
        return new File(rootDir, PHOENIX_ROOT_FOLDER + "phoenix-container/src/main/resources/" + fileName);
    }

    void createRuntimeResources(Workspace model) throws Exception {
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
        createLionProperties(resourceFileFor(projectDir, "servicelion-system.properties"), serviceLionCtxVisitor.getVisitResult());
        createEcliseLaunchFile(resourceFileFor(projectDir, "phoenix.launch"), launchFileContextVisitor.getVisitResult());
        createBytemanFile(resourceFileFor(projectDir, "service-lion.btm"));
    }

    File createSkeletonWorkspace(WorkspaceContext wsCtx) throws WorkspaceManagementException {
        return wms.create(wsCtx, System.out);
    }

    File modifySkeletonWorkspace(WorkspaceContext wsCtx) throws WorkspaceManagementException {
        return wms.modify(wsCtx, System.out);
    }

    void createRouterRuleXml(File routerRulesFile, RouterRuleContext ctx) throws IOException {
        routerGenerator.generate(routerRulesFile, ctx);
    }

    void createBizServerProperties(File bizServerFile, BizServerContext ctx) throws IOException {
        bizGenerator.generate(bizServerFile, ctx);
    }

    void createLionProperties(File lionFile, ServiceLionContext ctx) throws Exception {
        lionGenerator.generate(lionFile, ctx);
    }

    void createEcliseLaunchFile(File launchFile, LaunchFileContext ctx) throws Exception {
        launchGenerator.generate(launchFile, ctx);
    }

    void createBytemanFile(File bytemanFile) throws Exception {
        bytemanGenerator.generate(bytemanFile, new HashMap<String, String>());
    }
}

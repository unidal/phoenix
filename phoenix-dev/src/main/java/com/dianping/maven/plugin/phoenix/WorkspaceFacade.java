package com.dianping.maven.plugin.phoenix;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.unidal.lookup.annotation.Inject;

import com.dianping.maven.plugin.configure.Whiteboard;
import com.dianping.maven.plugin.phoenix.model.entity.Workspace;
import com.dianping.maven.plugin.phoenix.model.transform.DefaultSaxParser;
import com.dianping.maven.plugin.tools.generator.BytemanScriptGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.BizServerContext;
import com.dianping.maven.plugin.tools.generator.dynamic.BizServerPropertiesGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.F5Manager;
import com.dianping.maven.plugin.tools.generator.dynamic.LaunchFileContext;
import com.dianping.maven.plugin.tools.generator.dynamic.LaunchFileGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.ServiceLionContext;
import com.dianping.maven.plugin.tools.generator.dynamic.ServiceLionPropertiesGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.UrlRuleContext;
import com.dianping.maven.plugin.tools.generator.dynamic.UrlRuleGenerator;
import com.dianping.maven.plugin.tools.generator.dynamic.model.visitor.BizServerContextVisitor;
import com.dianping.maven.plugin.tools.generator.dynamic.model.visitor.LaunchFileContextVisitor;
import com.dianping.maven.plugin.tools.generator.dynamic.model.visitor.RouterRuleContextVisitor;
import com.dianping.maven.plugin.tools.generator.dynamic.model.visitor.ServiceLionContextVisitor;
import com.dianping.maven.plugin.tools.generator.dynamic.model.visitor.WorkspaceContextVisitor;
import com.dianping.maven.plugin.tools.vcs.RepositoryService;
import com.dianping.maven.plugin.tools.wms.RepositoryManager;
import com.dianping.maven.plugin.tools.wms.WorkspaceConstants;
import com.dianping.maven.plugin.tools.wms.WorkspaceContext;
import com.dianping.maven.plugin.tools.wms.WorkspaceManagementException;
import com.dianping.maven.plugin.tools.wms.WorkspaceManagementService;

public class WorkspaceFacade {

    private static Logger                  log = Logger.getLogger(WorkspaceFacade.class);

    @Inject
    private WorkspaceManagementService     wms;
    @Inject
    private ServiceLionPropertiesGenerator lionGenerator;
    @Inject
    private LaunchFileGenerator            launchGenerator;
    @Inject
    private BizServerPropertiesGenerator   bizGenerator;
    @Inject
    private UrlRuleGenerator               routerGenerator;
    @Inject
    private F5Manager                      f5Mgr;
    @Inject
    private RepositoryService              repositoryService;
    @Inject
    private BytemanScriptGenerator         bytemanGenerator;
    @Inject
    private RepositoryManager              repoMgr;

    public void init(File wsDir) {
        pullConfig(wsDir);
        Whiteboard.INSTANCE.workspaceInitialized(wsDir);
    }

    public List<String> getProjectListByPrefix(String prefix) {
        return repoMgr.getProjectListByPrefix(prefix);
    }

    public void create(Workspace model) throws Exception {
        workspaceChange(model, false);
    }

    public Workspace current(File dir) throws Exception {
        File metaFile = new File(dir, WorkspaceConstants.WORKSPACE_META_FILENAME);
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

        FileUtils.forceMkdir(new File(model.getDir(), WorkspaceConstants.PHOENIX_ROOT_FOLDER));

        if (modify) {
            modifySkeletonWorkspace(workspaceCtxVisitor.getVisitResult());
        } else {
            createSkeletonWorkspace(workspaceCtxVisitor.getVisitResult());
        }
        createRuntimeResources(model);
        saveMeta(model);
        FileUtils.touch(new File(model.getDir(), WorkspaceConstants.REINIT_SIG_FILENAME));
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

    private void saveMeta(Workspace model) throws Exception {
        FileUtils.writeStringToFile(new File(model.getDir(), WorkspaceConstants.WORKSPACE_META_FILENAME),
                model.toString(), "utf-8");
    }

    private File resourceFileFor(File rootDir, String fileName) {
        return new File(rootDir, WorkspaceConstants.PHOENIX_RESOURCE_FOLDER + fileName);
    }

    private File rootFileFor(File rootDir, String fileName) {
        return new File(rootDir, WorkspaceConstants.PHOENIX_CONTAINER_FOLDER + fileName);
    }

    private File metaFileFor(File rootDir, String fileName) {
        return new File(rootDir, WorkspaceConstants.PHOENIX_META_FOLDER + fileName);
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

        createUrlRuleXml(resourceFileFor(projectDir, ""), routerRuleCtxVisitor.getVisitResult());
        createBizServerProperties(resourceFileFor(projectDir, "phoenix.properties"),
                bizServerCtxVisitor.getVisitResult());
        createLionProperties(resourceFileFor(projectDir, "router-service.properties"),
                serviceLionCtxVisitor.getVisitResult());
        createEcliseLaunchFile(rootFileFor(projectDir, "phoenix.launch"), launchFileContextVisitor.getVisitResult());
        createBytemanFile(metaFileFor(projectDir, "service-lion.btm"));
        copyGitFileToClasspath(projectDir);
    }

    void copyGitFileToClasspath(File wsDir) throws IOException {
        File srcFile = new File(new File(wsDir, WorkspaceConstants.PHOENIX_CONFIG_FOLDER), "virtualServer.properties");
        File destDir = new File(wsDir, WorkspaceConstants.PHOENIX_RESOURCE_FOLDER);
        FileUtils.copyFileToDirectory(srcFile, destDir);
    }

    File createSkeletonWorkspace(WorkspaceContext wsCtx) throws WorkspaceManagementException {
        return wms.create(wsCtx, System.out);
    }

    File modifySkeletonWorkspace(WorkspaceContext wsCtx) throws WorkspaceManagementException {
        return wms.modify(wsCtx, System.out);
    }

    void createUrlRuleXml(File resourceDir, UrlRuleContext ctx) throws IOException {
        routerGenerator.generate(resourceDir, ctx);
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

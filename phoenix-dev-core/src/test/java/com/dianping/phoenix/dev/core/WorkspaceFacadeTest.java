package com.dianping.phoenix.dev.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.dev.core.model.workspace.entity.BizProject;
import com.dianping.phoenix.dev.core.model.workspace.entity.Workspace;
import com.dianping.phoenix.dev.core.model.workspace.transform.DefaultSaxParser;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.BizServerContext;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.F5Pool;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.LaunchFileContext;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.LaunchFileGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.ServiceLionContext;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.ServiceLionPropertiesGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.UrlRuleContext;
import com.dianping.phoenix.dev.core.tools.wms.WorkspaceContext;

public class WorkspaceFacadeTest extends ComponentTestCase {

    private static final String            UTF_8 = "utf-8";
    private WorkspaceFacade                facade;
    private ServiceLionPropertiesGenerator lionGenerator;
    private LaunchFileGenerator            lauchFileGenerator;

    @Before
    public void before() throws Exception {
        facade = lookup(WorkspaceFacade.class);
        lionGenerator = lookup(ServiceLionPropertiesGenerator.class);
        lauchFileGenerator = lookup(LaunchFileGenerator.class);
    }

    public void testCreateSkeletonWorkspace() throws Exception {
        WorkspaceContext workspaceCtx = new WorkspaceContext();
        File workSpaceDir = new File("/Users/leoleung/test");
        workspaceCtx.setBaseDir(workSpaceDir);
        workspaceCtx.setPhoenixRouterVersion("0.1-SNAPSHOT");
        List<String> projectNameList = Arrays.asList(new String[] { "user-web", "user-service", "user-base-service" });
        workspaceCtx.setProjects(convertToBizProjects(projectNameList));

        facade.createSkeletonWorkspace(workspaceCtx);

        assertTrue(new File(workSpaceDir, "user-web").exists());
        assertTrue(new File(workSpaceDir, "user-service").exists());
        assertTrue(new File(workSpaceDir, "user-base-service").exists());
    }

    private List<BizProject> convertToBizProjects(List<String> projectNames) {
        List<BizProject> res = new ArrayList<BizProject>();
        for (String name : projectNames) {
            BizProject p = new BizProject();
            p.setFrom("vcs");
            p.setName(name);
            res.add(p);
        }
        return res;
    }

    public void testCreateAll() throws Exception {
        String samplePath = "/com/dianping/phoenix/dev/core/model/workspace.xml";
        String workspaceXml = IOUtils.toString(this.getClass().getResourceAsStream(samplePath));
        Workspace model = DefaultSaxParser.parse(workspaceXml);
        facade.create(model);

        File wsDir = new File(model.getDir());

        assertTrue(new File(wsDir, "user-web").exists());
        assertTrue(new File(wsDir, "user-service").exists());
        assertTrue(new File(wsDir, "user-base-service").exists());

    }

    public void testCreateBizServerFile() throws IOException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File bizServerFile = new File(tmpDir, "bizServer.properties");
        BizServerContext ctx = new BizServerContext();
        ctx.addWebContext("/_user-web", "/a/b/c");
        facade.createBizServerProperties(bizServerFile, ctx);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(new FileInputStream(bizServerFile), out);
        String resultFile = new String(out.toByteArray(), UTF_8);
        assertTrue(resultFile.indexOf("/_user-web=/a/b/c") >= 0);
    }

    public void testCreateRouterRulesXml() throws IOException {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File routerRulesXml = new File(tmpDir, "router-rules.xml");

        UrlRuleContext ctx = new UrlRuleContext();
        ctx.addLocalPool(new F5Pool("", "Web.Web_X_Userweb", "http://127.0.0.1:8080/_user-web%s"));

        facade.createUrlRuleXml(routerRulesXml, ctx);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(new FileInputStream(routerRulesXml), out);

        String resultFile = new String(out.toByteArray(), "UTF-8");

        assertTrue(resultFile.indexOf("<pool name='Default' url-pattern='http://w.51ping.com%s' />") >= 0);
        assertTrue(resultFile
                .indexOf("<pool name='Web.Web_X_Userweb' url-pattern='http://127.0.0.1:8080/_user-web%s' />") >= 0);
    }

    public void testCreateLionPropertiesFile() throws Exception {
        Map<String, File> projectBaseDirMapping = new HashMap<String, File>();
        // projectBaseDirMapping.put("fuck", new
        // File("/Users/marsqing/Projects/tmp/phoenix-maven-tmp"));
        // ServiceMetaContext projectMetaContext = new
        // ServiceMetaContext("com.mysql.jdbc.Driver",
        // "jdbc:mysql://192.168.7.105:3306/hawk", "dpcom_hawk", "123456");
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File lionFile = new File(tmpDir, "phoenix-lion.btm");
        lionGenerator.generate(lionFile, new ServiceLionContext(projectBaseDirMapping, "127.0.0.1", null));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(new FileInputStream(lionFile), out);
        String resultFile = new String(out.toByteArray(), UTF_8);
        assertTrue(resultFile
                .indexOf("$this.pts.put(\"http://service.dianping.com/userBaseService/userProfileService_1.0.0\", \"127.0.0.1:2032\");") >= 0);
    }

    public void testCreateLaunchFile() throws Exception {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        File btmFile = new File(tmpDir, "phoenix-lion.btm");
        LaunchFileContext context = new LaunchFileContext("com.dianping.phoenix.PhoenixServer", btmFile);
        File launchFile = new File(tmpDir, "phoenix.launch");
        lauchFileGenerator.generate(launchFile, context);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(new FileInputStream(launchFile), out);
        String resultFile = new String(out.toByteArray(), UTF_8);
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
                        + "<launchConfiguration type=\"org.eclipse.jdt.launching.localJavaApplication\">\n"
                        + "<stringAttribute key=\"org.eclipse.jdt.launching.MAIN_TYPE\" value=\"com.dianping.phoenix.PhoenixServer\"/>\n"
                        + "<stringAttribute key=\"org.eclipse.jdt.launching.PROJECT_ATTR\" value=\"phoenix-container\"/>\n"
                        + "<stringAttribute key=\"org.eclipse.jdt.launching.VM_ARGUMENTS\" value=\"-Xmx1024m -Xms1024m -XX:MaxPermSize=256m -javaagent:src/main/resources/byteman-2.1.2.jar=script:/var/folders/dd/j2k_125543n5bfqk29yr4krc0000gn/T/phoenix-lion.btm\"/>\n"
                        + "</launchConfiguration>", resultFile);

    }

}

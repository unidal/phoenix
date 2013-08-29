package com.dianping.phoenix.dev.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.dev.core.model.workspace.entity.BizProject;
import com.dianping.phoenix.dev.core.model.workspace.entity.Workspace;
import com.dianping.phoenix.dev.core.model.workspace.transform.DefaultSaxParser;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.LaunchFileContext;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.LaunchFileGenerator;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.ServiceLionContext;
import com.dianping.phoenix.dev.core.tools.generator.dynamic.ServiceLionPropertiesGenerator;

public class WorkspaceFacadeTest extends ComponentTestCase {

    private static final String            UTF_8 = "utf-8";
    private WorkspaceFacade                facade;
    private ServiceLionPropertiesGenerator lionGenerator;
    private LaunchFileGenerator            lauchFileGenerator;

    @Before
    public void before() throws Exception {
        facade = lookup(WorkspaceFacade.class);
    }

    @Test
    public void testAgentCreate() throws Exception {
        String samplePath = "/com/dianping/phoenix/dev/core/model/workspace.xml";
        String workspaceXml = IOUtils.toString(this.getClass().getResourceAsStream(samplePath));
        Workspace model = DefaultSaxParser.parse(workspaceXml);
        model.setFrom("agent");
        model.getBizProjects().clear();
        BizProject bizProject = new BizProject();
        bizProject.setName("http://192.168.22.71/user-web-qa-0.0.3.war");
        model.addBizProject(bizProject);
        facade.init(new File("/Users/marsqing/Projects/tmp/test"));
        facade.create(model);
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

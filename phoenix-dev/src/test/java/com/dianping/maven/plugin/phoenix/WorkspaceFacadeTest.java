package com.dianping.maven.plugin.phoenix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;
import org.xml.sax.InputSource;

import com.dianping.maven.plugin.phoenix.model.entity.BizProject;
import com.dianping.maven.plugin.phoenix.model.entity.PhoenixProject;
import com.dianping.maven.plugin.phoenix.model.entity.Router;
import com.dianping.maven.plugin.phoenix.model.entity.Workspace;
import com.dianping.maven.plugin.phoenix.model.transform.DefaultSaxParser;
import com.dianping.maven.plugin.tools.misc.file.LaunchFileContext;
import com.dianping.maven.plugin.tools.misc.file.LaunchFileGenerator;
import com.dianping.maven.plugin.tools.misc.file.ServiceLionContext;
import com.dianping.maven.plugin.tools.misc.file.ServiceLionPropertiesGenerator;
import com.dianping.maven.plugin.tools.wms.WorkspaceContext;

public class WorkspaceFacadeTest extends ComponentTestCase {

	private static final String UTF_8 = "utf-8";
	private WorkspaceFacade facade;
	private ServiceLionPropertiesGenerator lionGenerator;
	private LaunchFileGenerator lauchFileGenerator;

	@Before
	public void before() throws Exception {
		facade = lookup(WorkspaceFacade.class);
		lionGenerator = lookup(ServiceLionPropertiesGenerator.class);
		lauchFileGenerator = lookup(LaunchFileGenerator.class);
	}

	public void testCreateSkeletonWorkspace() throws Exception {
		WorkspaceContext workspaceCtx = new WorkspaceContext();
		File workSpaceDir = new File("/Users/marsqing/Projects/tmp/phoenix-maven-tmp");
		workspaceCtx.setBaseDir(workSpaceDir);
		workspaceCtx.setPhoenixRouterVersion("0.1-SNAPSHOT");
		List<String> projectNameList = Arrays.asList(new String[] { "user-web", "user-service", "user-base-service" });
		workspaceCtx.setProjects(projectNameList);

		facade.createSkeletonWorkspace(workspaceCtx);

		assertTrue(new File(workSpaceDir, "user-web").exists());
		assertTrue(new File(workSpaceDir, "user-service").exists());
		assertTrue(new File(workSpaceDir, "user-base-service").exists());
	}

	@Test
	public void testCreateAll() throws Exception {
		// String samplePath =
		// "/com/dianping/maven/plugin/phoenix/model/workspace.xml";
		// String workspaceXml =
		// IOUtils.toString(this.getClass().getResourceAsStream(samplePath));
		// Workspace model = DefaultSaxParser.parse(workspaceXml);
		// facade.create(model);

		Workspace model = new Workspace();
		model.setDir("/Users/marsqing/Projects/tmp/phoenix-maven-tmp");
		BizProject bizProject = new BizProject();
		bizProject.setName("user-web");
		model.addBizProject(bizProject);

		PhoenixProject phoenixProject = new PhoenixProject();
		Router router = new Router();
		router.setDefaultUrlPattern("http://www.51ping.com%s");
		router.setPort(8080);
		router.setVersion("0.1-SNAPSHOT");
		phoenixProject.setRouter(router);
		model.setPhoenixProject(phoenixProject);

		try {
			facade.create(model);
		} catch (Exception e) {
			throw new MojoFailureException("error create phoenix workspace", e);
		}

		File wsDir = new File(model.getDir());

		assertTrue(new File(wsDir, "user-web").exists());
		assertTrue(new File(wsDir, "user-service").exists());
		assertTrue(new File(wsDir, "user-base-service").exists());

	}

	@Test
	public void testCreateBizServerFile() throws IOException {
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File bizServerFile = new File(tmpDir, "bizServer.properties");
		BizServerContext ctx = new BizServerContext();
		ctx.addWebContext("/_user-web", new File("/a/b/c"));
		facade.createBizServerProperties(bizServerFile, ctx);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(new FileInputStream(bizServerFile), out);
		String resultFile = new String(out.toByteArray(), UTF_8);
		assertTrue(resultFile.indexOf("/_user-web=/a/b/c") >= 0);
	}

	@Test
	public void testCreateRouterRulesXml() throws IOException {
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File routerRulesXml = new File(tmpDir, "router-rules.xml");

		RouterRuleContext ctx = new RouterRuleContext();
		ctx.setDefaultUrlPattern("http://w.51ping.com%s");
		ctx.addLocalPool(new F5Pool("", "Web.Web_X_Userweb", "http://127.0.0.1:8080/_user-web%s"));

		facade.createRouterRuleXml(routerRulesXml, ctx);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(new FileInputStream(routerRulesXml), out);

		String resultFile = new String(out.toByteArray(), "UTF-8");

		assertTrue(resultFile.indexOf("<pool name='Default' url-pattern='http://w.51ping.com%s' />") >= 0);
		assertTrue(resultFile
				.indexOf("<pool name='Web.Web_X_Userweb' url-pattern='http://127.0.0.1:8080/_user-web%s' />") >= 0);
	}

	@Test
	public void testCreateLionPropertiesFile() throws Exception {
		Map<String, File> projectBaseDirMapping = new HashMap<String, File>();
		// projectBaseDirMapping.put("fuck", new
		// File("/Users/marsqing/Projects/tmp/phoenix-maven-tmp"));
		// ServiceMetaContext projectMetaContext = new
		// ServiceMetaContext("com.mysql.jdbc.Driver",
		// "jdbc:mysql://192.168.7.105:3306/hawk", "dpcom_hawk", "123456");
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File lionFile = new File(tmpDir, "phoenix-lion.btm");
		lionGenerator.generate(lionFile, new ServiceLionContext(projectBaseDirMapping, null, "127.0.0.1", false));
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(new FileInputStream(lionFile), out);
		String resultFile = new String(out.toByteArray(), UTF_8);
		assertTrue(resultFile
				.indexOf("$this.pts.put(\"http://service.dianping.com/userBaseService/userProfileService_1.0.0\", \"127.0.0.1:2032\");") >= 0);
	}

	@Test
	public void testCreateLaunchFile() throws Exception {
		File tmpDir = new File(System.getProperty("java.io.tmpdir"));
		File btmFile = new File(tmpDir, "phoenix-lion.btm");
		LaunchFileContext context = new LaunchFileContext("com.dianping.phoenix.BizServer", btmFile);
		File launchFile = new File(tmpDir, "phoenix.launch");
		lauchFileGenerator.generate(launchFile, context);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(new FileInputStream(launchFile), out);
		String resultFile = new String(out.toByteArray(), UTF_8);
		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n"
						+ "<launchConfiguration type=\"org.eclipse.jdt.launching.localJavaApplication\">\n"
						+ "<stringAttribute key=\"org.eclipse.jdt.launching.MAIN_TYPE\" value=\"com.dianping.phoenix.BizServer\"/>\n"
						+ "<stringAttribute key=\"org.eclipse.jdt.launching.PROJECT_ATTR\" value=\"phoenix-container\"/>\n"
						+ "<stringAttribute key=\"org.eclipse.jdt.launching.VM_ARGUMENTS\" value=\"-Xmx1024m -Xms1024m -XX:MaxPermSize=256m -javaagent:src/main/resources/byteman-2.1.2.jar=script:/var/folders/dd/j2k_125543n5bfqk29yr4krc0000gn/T/phoenix-lion.btm\"/>\n"
						+ "</launchConfiguration>", resultFile);

	}

}

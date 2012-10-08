package com.dianping.phoenix.bootstrap.server;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.startup.Embedded;

import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader;
import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader.WebappProvider;

public abstract class AbstractTomcat6Bootstrap {
	protected String getCatalinaHome() {
		String catalinaHome = getClass().getResource("/tomcat6").getFile();

		return catalinaHome;
	}

	protected String getContextPath() {
		return "";
	}

	protected WebappProvider getKernelWebappProvider() throws IOException {
		return new MavenWebappProvider("../phoenix-kernel", "phoenix-kernel");
	}

	protected int getPort() {
		return 7463;
	}

	/**
	 * For example,
	 * 
	 * <pre>
	 * return new MavenWebappProvider(&quot;../sample-app1&quot;, &quot;sample-app1&quot;);
	 * </pre>
	 * 
	 * @return
	 * @throws IOException
	 */
	protected abstract WebappProvider getWebappProvider() throws IOException;

	protected void startTomcat() throws Exception {
		Embedded container = new Embedded();

		container.setCatalinaHome(getCatalinaHome());
		container.setRealm(new MemoryRealm());

		// create host
		Host localHost = container.createHost("localHost", new File(".").getAbsolutePath());
		Tomcat6WebappLoader loader = new Tomcat6WebappLoader(getClass().getClassLoader());
		WebappProvider kernelProvider = getKernelWebappProvider();
		WebappProvider appProvider = getWebappProvider();
		File warRoot = appProvider.getWarRoot();

		loader.setKernelWebappProvider(kernelProvider);
		loader.setApplicationWebappProvider(appProvider);

		Context context = container.createContext("/" + getContextPath(), warRoot.getCanonicalPath());
		context.setLoader(loader);

		// avoid write SESSIONS.ser to src/test/resources/
		StandardManager manager = new StandardManager();
		manager.setPathname(new File("target/session").getCanonicalFile().getPath());

		context.setManager(manager);
		context.setReloadable(true);

		localHost.addChild(context);

		// create engine
		Engine engine = container.createEngine();
		engine.setName("Phoenix");
		engine.addChild(localHost);
		engine.setDefaultHost(localHost.getName());
		container.addEngine(engine);

		// create http connector
		Connector httpConnector = container.createConnector((InetAddress) null, getPort(), false);

		container.addConnector(httpConnector);
		container.setAwait(true);

		// start server
		container.start();

		System.out.println("Press any key to stop the server ...");
		System.in.read();
		container.stop();
	}

	static class DevClasspathBuilder {
		public List<File> build(File libDir, File classesDir) {
			List<File> list = new ArrayList<File>();

			list.add(classesDir);

			if (libDir.isDirectory()) {
				String[] names = libDir.list();

				if (names != null) {
					for (String name : names) {
						File jarFile = new File(libDir, name);

						list.add(jarFile);
					}
				}
			}

			return list;
		}
	}

	protected static class MavenWebappProvider implements WebappProvider {
		private File m_classesDir;

		private File m_libDir;

		private File m_warRoot;

		public MavenWebappProvider(String baseDir, String finalName) throws IOException {
			m_classesDir = new File(baseDir, "target/classes").getCanonicalFile();
			m_libDir = new File(baseDir, "target/" + finalName + "/WEB-INF/lib").getCanonicalFile();
			m_warRoot = new File(baseDir, "src/main/webapp").getCanonicalFile();

			if (!m_warRoot.exists()) {
				throw new RuntimeException(String.format("Please make sure project at %s is a valid war "
				      + "with src/main/webapp folder!", baseDir));
			}

			if (!m_libDir.exists()) {
				throw new RuntimeException(String.format("You need to run 'mvn package' for project at %s once "
				      + "before starting the server!", baseDir));
			}
		}

		@Override
		public List<File> getClasspathEntries() {
			return new DevClasspathBuilder().build(m_libDir, m_classesDir);
		}

		@Override
		public File getWarRoot() {
			return m_warRoot;
		}
	}
}

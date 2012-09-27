package com.dianping.phoenix.bootstrap.server;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
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
	protected abstract String getBaseDir();

	protected String getCatalinaHome() {
		String catalinaHome = getClass().getResource("/tomcat6").getFile();

		return catalinaHome;
	}

	protected String getContextPath() {
		return "";
	}

	protected String getKernelWarRoot() {
		return getWarRoot("phoenix-kernel");
	}

	protected int getPort() {
		return 8080;
	}

	protected String getWarRoot(String projectName) {
		File warRoot = new File("../" + projectName, "src/main/webapp");

		try {
			return warRoot.getCanonicalPath();
		} catch (IOException e) {
			return warRoot.getPath();
		}
	}

	public void startTomcat() throws Exception {
		Embedded container = new Embedded();

		container.setCatalinaHome(getCatalinaHome());
		container.setRealm(new MemoryRealm());

		// create host
		Host localHost = container.createHost("localHost", new File(".").getAbsolutePath());
		Tomcat6WebappLoader loader = new Tomcat6WebappLoader(this.getClass().getClassLoader());
		WebappProvider kernelProvider = new WebappProvider() {
			@Override
			public List<File> getClasspathEntries() {
				return Arrays.asList(new File("../phoenix-kernel/target/classes")); // TODO
			}

			@Override
			public File getWarRoot() {
				return new File(String.format("%s/src/main/webapp", "../phoenix-kernel"));
			}
		};
		WebappProvider appProvider = new WebappProvider() {
			@Override
			public List<File> getClasspathEntries() {
				return Arrays.asList(new File(String.format("%s/target/classes", getBaseDir())));
				// TODO
			}

			@Override
			public File getWarRoot() {
				return new File(String.format("%s/src/main/webapp", getBaseDir()));
			}
		};

		loader.setKernelWebappProvider(kernelProvider);
		loader.setApplicationWebappProvider(appProvider);

		Context context = container.createContext("/" + getContextPath(), appProvider.getWarRoot().getCanonicalPath());
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

		try {
			// start server
			container.start();

			System.out.println("Press any key to stop the server ...");
			System.in.read();
		} finally {
			container.stop();
		}
	}
}

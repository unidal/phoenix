package com.dianping.phoenix.bootstrap.tomcat6;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.startup.Embedded;
import org.junit.Test;

import com.dianping.phoenix.bootstrap.Tomcat6WebappLoader;

/**
 * Utility class for starting tomcat via api
 * 
 * @author marsqing
 * 
 */
public class TomcatBootstrap {

	private Embedded container = null;

	private Host localHost;

	private CountDownLatch shutdownLatch = new CountDownLatch(1);

	private String kernelWarRoot;

	/**
	 * The directory to create the Tomcat server configuration under.
	 */
	private String catalinaHome;

	private int port = 8080;

	private List<TomcatWebappConfig> webappConfig;

	/**
	 * 
	 * @param catalinaHome
	 *            directory which contains a children directory named conf which
	 *            contains all files from a standard tomcat installation's conf
	 *            directory
	 * @param port
	 *            port to listen
	 * @param webappConfig
	 *            web apps to start
	 */
//	public TomcatBootstrap(String catalinaHome, int port, List<TomcatWebappConfig> webappConfig, String kernelWarRoot) {
//		this.catalinaHome = catalinaHome;
//		this.port = port;
//		this.webappConfig = webappConfig;
//		this.kernelWarRoot = kernelWarRoot;
//	}
	
	/**
	 * fake, for unit test only
	 */
	public TomcatBootstrap() {
	}

	public void startTomcat() throws Exception {
		// create server
		container = new Embedded();
		container.setCatalinaHome(catalinaHome);
		container.setRealm(new MemoryRealm());

		// create host
		localHost = container.createHost("localHost", new File(".").getAbsolutePath());
		for (TomcatWebappConfig config : webappConfig) {
			addWebapp(config);
		}

		// create engine
		Engine engine = container.createEngine();
		engine.setName("localEngine");
		engine.addChild(localHost);
		engine.setDefaultHost(localHost.getName());
		container.addEngine(engine);

		// create http connector
		Connector httpConnector = container.createConnector((InetAddress) null, port, false);
		container.addConnector(httpConnector);

		container.setAwait(true);

		// start server
		container.start();

		// add shutdown hook to stop server
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					stopTomcat();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		shutdownLatch.await();
	}

	/**
	 * Add a web app
	 * 
	 * @param config
	 * @throws MalformedURLException
	 */
	public void addWebapp(TomcatWebappConfig config) throws MalformedURLException {
		Tomcat6WebappLoader loader = new Tomcat6WebappLoader(this.getClass().getClassLoader());

//		loader.setKernelWarRoot(kernelWarRoot);

		if (config.getClassesDir() != null) {
			for (String classDir : config.getClassesDir()) {
				loader.addRepository(new File(classDir).toURI().toURL().toString());
			}
		}

		Context context = container.createContext("/" + config.getContextPath(), config.getWebappDir());
		context.setLoader(loader);
		// avoid write SESSIONS.ser to src/test/resources/
		StandardManager manager = new StandardManager();
		manager.setPathname(System.getProperty("java.io.tmpdir"));
		context.setManager(manager);
		context.setReloadable(true);

		localHost.addChild(context);
	}

	public void stopTomcat() throws Exception {
		shutdownLatch.countDown();
	}

	public static void main(String[] args) throws Exception {
		innerStart(args);
	}
	
	@Test
	public void testServer() throws Exception {
		String[] args = new String[2];
		String phoenixProjectRoot = System.getProperty("phoenixProjectRoot");
		String kernelWarRoot = System.getProperty("kernelWarRoot");
		if(phoenixProjectRoot != null && kernelWarRoot != null) {
			args[0] = phoenixProjectRoot;
			args[1] = kernelWarRoot;
			innerStart(args);
		} else {
			innerStart(null);
		}
	}
	
	private static void innerStart(String[] args) throws Exception {
		String phoenixProjectRoot = "../";
		String kernelWarRoot = "../kernel/target/kernel/";

		if (args != null && args.length > 0) {
			if (args.length != 2) {
				System.out.println("Usage: phoenixProjectRoot kernelWarRoot");
				System.exit(1);
			} else {
				phoenixProjectRoot = args[0];
				kernelWarRoot = args[1];
			}
		}

		System.out.println("Starting tomcat with phoenix project root dir " + new File(phoenixProjectRoot).getAbsolutePath());
		System.out.println("\t\t and kernel war root dir " + new File(kernelWarRoot).getAbsolutePath());

		final TomcatWebappConfig config = new TomcatWebappConfig();
		final String extraClassesDir = phoenixProjectRoot + "/samples/target/classes";
		if ((new File(extraClassesDir)).exists()) {
			config.setClassesDir(Arrays.asList(extraClassesDir));
		}
		config.setContextPath("");
		config.setWebappDir(phoenixProjectRoot + "/samples/src/main/webapp");

		final TomcatBootstrap inst = new TomcatBootstrap();
		inst.setCatalinaHome(phoenixProjectRoot + "/bootstrap/src/test/resources/tomcat6");
		inst.setKernelWarRoot(kernelWarRoot);
		inst.setPort(8080);
		inst.setWebappConfig(Arrays.asList(config));

		new Thread() {
			public void run() {
				try {
					inst.startTomcat();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

		System.out.println("Press any key to stop ...");
		System.in.read();
		inst.stopTomcat();
	}

	public void setKernelWarRoot(String kernelWarRoot) {
		this.kernelWarRoot = kernelWarRoot;
	}

	public void setCatalinaHome(String catalinaHome) {
		this.catalinaHome = catalinaHome;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setWebappConfig(List<TomcatWebappConfig> webappConfig) {
		this.webappConfig = webappConfig;
	}
	
}

package com.dianping.phoenix.bootstrap.tomcat6;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.startup.Embedded;

/**
 * Utility class for starting tomcat via api
 * @author marsqing
 *
 */
public class TomcatBootstrap {

	private Embedded container = null;
	private CountDownLatch shutdownLatch = new CountDownLatch(1);

	/**
	 * The directory to create the Tomcat server configuration under.
	 */
	private String catalinaHome;
	private int port = 8080;
	private List<TomcatWebappConfig> webappConfig;

	/**
	 * 
	 * @param catalinaHome directory which contains a children directory named conf which contains all files
	 * from a standard tomcat installation's conf directory
	 * @param port port to listen
	 * @param webappConfig web apps to start
	 */
	public TomcatBootstrap(String catalinaHome, int port, List<TomcatWebappConfig> webappConfig) {
		this.catalinaHome = catalinaHome;
		this.port = port;
		this.webappConfig = webappConfig;
	}

	public void startTomcat() throws Exception {
		// create server
		container = new Embedded();
		container.setCatalinaHome(catalinaHome);
		container.setRealm(new MemoryRealm());

		// create host
		Host localHost = container.createHost("localHost", new File(".").getAbsolutePath());
		prepareContext(localHost);

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

	private void prepareContext(Host localHost) throws MalformedURLException {

		for (TomcatWebappConfig config : webappConfig) {
			WebappLoader loader = new WebappLoader(this.getClass().getClassLoader());
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
	}

	public void stopTomcat() throws Exception {
		shutdownLatch.countDown();
	}

	public static void main(String[] args) throws Exception {
		final String baseDir = "../";
		
		final TomcatWebappConfig config = new TomcatWebappConfig();
		config.setClassesDir(new ArrayList<String>() {
			{
				add(baseDir + "/samples/target/classes");
			}
		});
		config.setContextPath("");
		config.setWebappDir(baseDir + "/samples/src/test/resources");
		
		final TomcatBootstrap inst = new TomcatBootstrap(baseDir + "/bootstrap/src/test/resources/tomcat6", 8089,
				new ArrayList<TomcatWebappConfig>() {
					{
						add(config);
					}
				});
		
		new Thread() {
			public void run() {
				try {
					inst.startTomcat();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		
		System.in.read();
		inst.stopTomcat();
	}

}

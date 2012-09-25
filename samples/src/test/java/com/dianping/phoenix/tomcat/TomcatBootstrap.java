package com.dianping.phoenix.tomcat;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.realm.MemoryRealm;
import org.apache.catalina.startup.Embedded;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;


public class TomcatBootstrap {

    private String path = null;
    private Embedded container = null;
    private Log logger = LogFactory.getLog(getClass());

    /**
     * The directory to create the Tomcat server configuration under.
     */
    private String catalinaHome = "/Users/marsqing/Downloads/apache-tomcat-6.0.35/";
    
    /**
     * The web resources directory for the web application being run.
     */
    private String webappDir = "/Users/marsqing/Projects/tmp/ph";

    /**
     * The port to run the Tomcat server on.
     */
    private int port = 8089;

    /**
     * The classes directory for the web application being run.
     */
    private String classesDir = "target/classes";

    /**
     * Creates a single-webapp configuration to be run in Tomcat on port 8089. If module name does
     * not conform to the 'contextname-webapp' convention, use the two-args constructor.
     * 
     * @param contextName without leading slash, for example, "mywebapp"
     * @throws IOException
     */
    public TomcatBootstrap(String contextName) {
//        Assert.isTrue(!contextName.startsWith("/"));
        path = "/" + contextName;
    }

    /**
     * Starts the embedded Tomcat server.
     * 
     * @throws LifecycleException
     * @throws MalformedURLException if the server could not be configured
     * @throws LifecycleException if the server could not be started
     * @throws MalformedURLException
     */
    public void run(int port) throws LifecycleException, MalformedURLException {
        this.port = port;
        // create server
        container = new Embedded();
        container.setCatalinaHome(catalinaHome);
        container.setRealm(new MemoryRealm());

        // create webapp loader
        WebappLoader loader = new WebappLoader(this.getClass().getClassLoader());

        if (classesDir != null) {
            loader.addRepository(new File(classesDir).toURI().toURL().toString());
        }

        // create context
        // TODO: Context rootContext = container.createContext(path, webappDir);
        Context rootContext = container.createContext(path, webappDir);
        rootContext.setLoader(loader);
        rootContext.setReloadable(true);

        // create host
        // String appBase = new File(catalinaHome, "webapps").getAbsolutePath();
        Host localHost = container.createHost("localHost", new File("target").getAbsolutePath());
        localHost.addChild(rootContext);

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
                stopContainer();
            }
        });
    }
    /**
     * Stops the embedded Tomcat server.
     */
    public void stopContainer() {
        try {
            if (container != null) {
                container.stop();
            }
        } catch (LifecycleException exception) {
            logger.warn("Cannot Stop Tomcat" + exception.getMessage());
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static void main(String[] args) throws Exception {
        TomcatBootstrap inst = new TomcatBootstrap("");
        inst.run(8089);
        System.in.read();
    }

    public int getPort() {
        return port;
    }

}

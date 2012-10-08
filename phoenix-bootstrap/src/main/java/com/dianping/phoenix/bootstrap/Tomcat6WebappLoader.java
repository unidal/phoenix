package com.dianping.phoenix.bootstrap;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javax.servlet.ServletContext;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.loader.WebappClassLoader;
import org.apache.catalina.loader.WebappLoader;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

/**
 * A WebappLoader that allows a customized classpath to be added through
 * configuration in context xml. Any additional classpath entry will be added to
 * the default webapp classpath, making easy to emulate a standard webapp
 * without the need for assembly all the webapp dependencies as jars in
 * WEB-INF/lib.
 * 
 * <pre>
 * &lt;Context docBase="\webapps\mydocbase">
 *   &lt;Loader className="com.dianping.phoenix.bootstrap.Tomcat6WebappLoader"/>
 * &lt;/Context>
 * </pre>
 */
public class Tomcat6WebappLoader extends WebappLoader {
	private static Log m_log = LogFactory.getLog(Tomcat6WebappLoader.class);

	private WebappProvider m_appProvider;

	private WebappProvider m_kernelProvider;

	public Tomcat6WebappLoader() {
	}

	public Tomcat6WebappLoader(ClassLoader classloader) {
		super(classloader);
	}

	ClassLoader getBootstrapClassloader() {
		try {
			List<URL> urls = new ArrayList<URL>();

			for (File entry : m_kernelProvider.getClasspathEntries()) {
				File file = entry.getCanonicalFile();

				if (file.isDirectory() || file.getPath().endsWith(".jar")) {
					urls.add(file.toURI().toURL());
				}
			}

			m_log.info("Bootstrap class path: " + urls);
			return new URLClassLoader(urls.toArray(new URL[0]));
		} catch (Exception e) {
			throw new RuntimeException("Unable to create bootstrap classloader for kernel war!", e);
		}
	}

	public File getKernelWarRoot() {
		return m_kernelProvider.getWarRoot();
	}

	public ServletContext getServletContext() {
		Container container = super.getContainer();

		if (container instanceof Context) {
			ServletContext servletContext = ((Context) container).getServletContext();

			return servletContext;
		} else {
			throw new RuntimeException("No ServletContext was found!");
		}
	}

	public File getWarRoot() {
		return m_appProvider.getWarRoot();
	}

	/**
	 * The webapp class loader should be used to load all classes for the runtime
	 * request.
	 * 
	 * @return webapp class loader
	 */
	public WebappClassLoader getWebappClassLoader() {
		WebappClassLoader classLoader = (WebappClassLoader) super.getClassLoader();

		if (classLoader != null) {
			return classLoader;
		} else {
			throw new IllegalStateException("WebappClassLoader is not ready at this time!");
		}
	}

	Configurator loadConfigurator(ClassLoader classloader) {
		if (classloader == null) {
			classloader = Thread.currentThread().getContextClassLoader();
		}

		ServiceLoader<Configurator> serviceLoader = ServiceLoader.load(Configurator.class, classloader);

		for (Configurator e : serviceLoader) {
			return e;
		}

		throw new UnsupportedOperationException("No implementation class found for " + Configurator.class);
	}

	public void setApplicationWebappProvider(WebappProvider appProvider) {
		m_appProvider = appProvider;
	}

	public void setKernelWebappProvider(WebappProvider kernelProvider) {
		m_kernelProvider = kernelProvider;
	}

	@Override
	public void start() throws LifecycleException {
		init();

		ClassLoader bootstrapClassloader = getBootstrapClassloader();
		Configurator configurator = loadConfigurator(bootstrapClassloader);

		configurator.configure(this);
		super.start();
		configurator.postConfigure(this);
	}

	public static interface Configurator {
		/**
		 * Configure the tomcat6 webapp loader before it starts.
		 * 
		 * @param loader
		 */
		public void configure(Tomcat6WebappLoader loader);

		/**
		 * Configure the tomcat6 webapp loader after it starts.
		 * 
		 * @param loader
		 */
		public void postConfigure(Tomcat6WebappLoader loader);
	}

	public static interface WebappProvider {
		public List<File> getClasspathEntries();

		public File getWarRoot();
	}
}

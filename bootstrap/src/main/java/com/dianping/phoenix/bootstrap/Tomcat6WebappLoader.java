package com.dianping.phoenix.bootstrap;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
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
 *   &lt;Loader className="com.dianping.phoenix.bootstrap.Tomcat6WebappLoader" kernelWebappRoot="/data/webapps/kernel/current/kernel.war/"/>
 * &lt;/Context>
 * </pre>
 */
public class Tomcat6WebappLoader extends WebappLoader {
	private String m_kernelWarRoot = "/data/webapps/kernel/current/kernel.war/";

	private String m_warRoot;

	private static Log m_log = LogFactory.getLog(Tomcat6WebappLoader.class);

	public Tomcat6WebappLoader() {
	}

	public Tomcat6WebappLoader(ClassLoader classloader) {
		super(classloader);
	}

	ClassLoader getBootstrapClassloader() {
		try {
			List<URL> urls = new ArrayList<URL>();
			File classesDir = new File(m_kernelWarRoot, "WEB-INF/classes");
			File libDir = new File(m_kernelWarRoot, "WEB-INF/lib");

			if (classesDir.isDirectory()) {
				urls.add(classesDir.toURI().toURL());
			} else {
				m_log.warn(String.format("Directory(%s) is not found! IGNORED.", classesDir));
			}

			if (libDir.isDirectory()) {
				String[] files = libDir.list();

				for (String file : files) {
					if (file.endsWith(".jar")) {
						File jarFile = new File(libDir, file);

						urls.add(jarFile.toURI().toURL());
					}
				}
			} else {
				m_log.warn(String.format("Directory(%s) is not found! IGNORED.", libDir));
			}

			m_log.info("Bootstrap class path: " + urls);
			return new URLClassLoader(urls.toArray(new URL[0]));
		} catch (MalformedURLException e) {
			throw new RuntimeException(String.format(
			      "Unable to create bootstrap classloader for kernel war! kernelWarRoot: %s.", m_kernelWarRoot), e);
		}
	}

	public String getKernelWarRoot() {
		return m_kernelWarRoot;
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

	public String getWarRoot() {
		return m_warRoot;
	}

	/**
	 * The webapp class loader should be used to load all classes for the runtime
	 * request.
	 * 
	 * @return webapp class loader
	 */
	public WebappClassLoader getWebappClassLoader() {
		return (WebappClassLoader) super.getClassLoader();
	}

	Configurator loadConfigurator(ClassLoader classloader) {
		if (classloader == null) {
			classloader = Thread.currentThread().getContextClassLoader();
		}

		ServiceLoader<Configurator> serviceLoader = ServiceLoader.load(Configurator.class, classloader);

		for (Configurator e : serviceLoader) {
			return e;
		}

		throw new UnsupportedOperationException("No implemenation class found for " + Configurator.class);
	}

	public void setKernelWarRoot(String kernelWebapp) {
		try {
			m_kernelWarRoot = new File(kernelWebapp).getCanonicalPath();
		} catch (IOException e) {
			m_kernelWarRoot = kernelWebapp;
		}
	}

	@Override
	public void start() throws LifecycleException {
		String warRoot = getServletContext().getRealPath("/");

		try {
			m_warRoot = new File(warRoot).getCanonicalPath();
		} catch (IOException e) {
			m_warRoot = warRoot;
		}

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
}

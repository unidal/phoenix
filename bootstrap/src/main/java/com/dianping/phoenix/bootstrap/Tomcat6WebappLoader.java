package com.dianping.phoenix.bootstrap;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;

import javax.servlet.ServletContext;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.loader.WebappLoader;

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

	public Tomcat6WebappLoader() {
	}

	public Tomcat6WebappLoader(ClassLoader classloader) {
		super(classloader);
	}

	ClassLoader getBootstrapClassloader() {
		try {
			URL[] urls = new URL[1];

			urls[0] = new File(m_kernelWarRoot, "WEB-INF/classes").toURI().toURL();
			return new URLClassLoader(urls);
		} catch (MalformedURLException e) {
			throw new RuntimeException(String.format("Unable to create bootstrap classloader from kernel war! path: %s.",
			      m_kernelWarRoot), e);
		}
	}

	public String getWarRoot() {
		return m_warRoot;
	}

	public String getKernelWarRoot() {
		return m_kernelWarRoot;
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

	public void setKernelWebapp(String kernelWebapp) {
		m_kernelWarRoot = kernelWebapp;
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

	@Override
	public void start() throws LifecycleException {
		m_warRoot = getServletContext().getRealPath("/");

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

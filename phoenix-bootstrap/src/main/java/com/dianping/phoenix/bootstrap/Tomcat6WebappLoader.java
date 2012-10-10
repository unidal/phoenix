package com.dianping.phoenix.bootstrap;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

import javax.servlet.ServletContext;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
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

	private ClassLoader m_webappClassloader;

	private boolean m_debug = true;
	
	private Configurator configurator;

	public Tomcat6WebappLoader() {
	}

	public Tomcat6WebappLoader(ClassLoader classloader) {
		super(classloader);
	}

	WebappClassLoader adjustWebappClassloader(WebappClassLoader classloader) {
		try {
			List<File> entries = new ArrayList<File>(256);

			entries.addAll(m_kernelProvider.getClasspathEntries());
			entries.addAll(m_appProvider.getClasspathEntries());

			for (File entry : entries) {
				File file = entry.getCanonicalFile();

				if (file.isDirectory() || file.getPath().endsWith(".jar")) {
					// no phoenix-kernel/target/classes
					// since it's for bootstrap classloader only by design
					if (m_debug || !file.getPath().contains("/phoenix-kernel/target/classes")) {
						classloader.addRepository(file.toURI().toURL().toExternalForm());
					}
				}
			}

			m_log.info(String.format("Webapp classpath: %s.", Arrays.asList(classloader.getURLs())));
			return classloader;
		} catch (Exception e) {
			throw new RuntimeException("Error when adjusting webapp classloader!", e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getFieldValue(Class<?> clazz, String fieldName, Object instance) throws Exception {
		Field field = clazz.getDeclaredField(fieldName);

		if (!field.isAccessible()) {
			field.setAccessible(true);
		}

		return (T) field.get(instance);
	}

	ClassLoader createBootstrapClassloader() {
		try {
			List<URL> urls = new ArrayList<URL>();

			for (File entry : m_kernelProvider.getClasspathEntries()) {
				File file = entry.getCanonicalFile();

				if (file.isDirectory() || file.getPath().endsWith(".jar")) {
					urls.add(file.toURI().toURL());
				}
			}

			m_log.info(String.format("Bootstrap classpath: %s.", urls));
			return new URLClassLoader(urls.toArray(new URL[0]));
		} catch (Exception e) {
			throw new RuntimeException("Unable to create bootstrap classloader!", e);
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
		if (m_webappClassloader != null) {
			return (WebappClassLoader) m_webappClassloader;
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

		super.start();

		m_webappClassloader = adjustWebappClassloader((WebappClassLoader) getClassLoader());
	}
	
	@Override
	public void setContainer(Container container) {
		super.setContainer(container);
		configurator = loadConfigurator(createBootstrapClassloader());
		configurator.configure(this);
	}

	public static interface Configurator {
		
		/**
		 * Configure the tomcat6 webapp loader before it starts.
		 * 
		 * @param loader
		 */
		public void configure(Tomcat6WebappLoader loader);

	}

	public static interface WebappProvider {
		public List<File> getClasspathEntries();

		public File getWarRoot();
	}
}

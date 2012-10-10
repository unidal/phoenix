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
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
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
	
	private String m_kernelDocBase;

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
					classloader.addRepository(file.toURI().toURL().toExternalForm());
				}
			}

			m_log.info(String.format("Webapp classpath: %s.", Arrays.asList(classloader.getURLs())));
			return classloader;
		} catch (Exception e) {
			throw new RuntimeException("Error when adjusting webapp classloader!", e);
		}
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

	@SuppressWarnings("unchecked")
	public <T> T getFieldValue(Class<?> clazz, String fieldName, Object instance) throws Exception {
		Field field = clazz.getDeclaredField(fieldName);

		if (!field.isAccessible()) {
			field.setAccessible(true);
		}

		return (T) field.get(instance);
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

	Listener loadListener(ClassLoader classloader) {
		if (classloader == null) {
			classloader = Thread.currentThread().getContextClassLoader();
		}

		ServiceLoader<Listener> serviceLoader = ServiceLoader.load(Listener.class, classloader);

		for (Listener e : serviceLoader) {
			return e;
		}

		throw new UnsupportedOperationException("No implementation class found in phoenix-kernel war for "
		      + Listener.class);
	}

	/**
	 * For development only!
	 * 
	 * @param appProvider
	 */
	public void setApplicationWebappProvider(WebappProvider appProvider) {
		m_appProvider = appProvider;
	}

	@Override
	public void setContainer(Container container) {
		super.setContainer(container);

		Listener listener = loadListener(createBootstrapClassloader());

		((StandardContext) container).addLifecycleListener(new Delegate(this, listener));
	}

	public void setDebug(String debug) {
		m_debug = "true".equals(debug);
	}

	/**
	 * For production only!
	 * 
	 * @param kernelDocBase
	 */
	public void setKernelDocBase(String kernelDocBase) {
		m_kernelDocBase = kernelDocBase;
	}

	/**
	 * For development only!
	 * 
	 * @param kernelProvider
	 */
	public void setKernelWebappProvider(WebappProvider kernelProvider) {
		m_kernelProvider = kernelProvider;
	}

	@Override
	public void start() throws LifecycleException {
		super.start();

		m_webappClassloader = adjustWebappClassloader((WebappClassLoader) getClassLoader());
	}

	public static interface ClasspathMerger {
		public List<File> merge(List<File> kernelEntries, List<File> appEntries);
	}

	public class Delegate implements LifecycleListener {
		private Tomcat6WebappLoader m_loader;

		private Listener m_listener;

		public Delegate(Tomcat6WebappLoader loader, Listener listener) {
			m_loader = loader;
			m_listener = listener;
		}

		@Override
		public void lifecycleEvent(LifecycleEvent event) {
			String type = event.getType();

			try {
				if (Lifecycle.INIT_EVENT.equals(type)) {
					m_listener.initializing(m_loader);
				} else if (Lifecycle.BEFORE_START_EVENT.equals(type)) {
					m_listener.beforeStarting(m_loader);
				} else if (Lifecycle.START_EVENT.equals(type)) {
					m_listener.starting(m_loader);
				} else if (Lifecycle.AFTER_START_EVENT.equals(type)) {
					m_listener.afterStarted(m_loader);
				} else if (Lifecycle.STOP_EVENT.equals(type)) {
					m_listener.stopping(m_loader);
				} else if (Lifecycle.DESTROY_EVENT.equals(type)) {
					m_listener.destroying(m_loader);
				} else {
					// ignore it
				}
			} catch (Throwable e) {
				m_log.error(String.format("Error when dispatching lifecycle event(%s) to listener(%s)!", type, m_listener
				      .getClass().getName()), e);
			}
		}
	}

	public static interface Listener {
		public void afterStarted(Tomcat6WebappLoader loader);

		public void beforeStarting(Tomcat6WebappLoader loader);

		public void destroying(Tomcat6WebappLoader loader);

		public void initializing(Tomcat6WebappLoader loader);

		public void starting(Tomcat6WebappLoader loader);

		public void stopping(Tomcat6WebappLoader loader);
	}

	public static interface WebappProvider {
		public List<File> getClasspathEntries();

		public File getWarRoot();
	}
}

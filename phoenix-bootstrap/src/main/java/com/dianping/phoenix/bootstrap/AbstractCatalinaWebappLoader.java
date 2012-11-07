package com.dianping.phoenix.bootstrap;

import java.io.File;
import java.io.IOException;
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

import com.dianping.phoenix.spi.ClasspathBuilder;
import com.dianping.phoenix.spi.WebappProvider;
import com.dianping.phoenix.spi.internal.DefaultClasspathBuilder;
import com.dianping.phoenix.spi.internal.StandardWebappProvider;

public abstract class AbstractCatalinaWebappLoader extends WebappLoader {
	public static final String PHOENIX_WEBAPP_LOADER = "phoenix.webapp.loader";

	public static final String PHOENIX_WEBAPP_PROVIDER_APP = "phoenix.webapp.provider.app";

	public static final String PHOENIX_WEBAPP_PROVIDER_KERNEL = "phoenix.webapp.provider.kernel";

	private WebappProvider m_appProvider;

	private WebappProvider m_kernelProvider;

	private ClassLoader m_webappClassloader;

	private String m_kernelDocBase;

	private File m_webXml;

	private boolean m_debug = true;

	public AbstractCatalinaWebappLoader() {
	}

	public AbstractCatalinaWebappLoader(ClassLoader classloader) {
		super(classloader);
	}

	protected WebappClassLoader adjustWebappClassloader(WebappClassLoader classloader) {
		try {
			clearLoadedJars(classloader);

			ClasspathBuilder builder = new DefaultClasspathBuilder();
			List<URL> urls = builder.build(m_kernelProvider, m_appProvider);

			for (URL url : urls) {
				super.addRepository(url.toExternalForm());
			}

			if (m_debug) {
				getLog().info(String.format("Webapp classpath: %s.", Arrays.asList(classloader.getURLs())));
			}

			Container container = getContainer();
			ServletContext ctx = ((StandardContext) container).getServletContext();

			// pass followings to application
			ctx.setAttribute(PHOENIX_WEBAPP_LOADER, this);
			ctx.setAttribute(PHOENIX_WEBAPP_PROVIDER_KERNEL, m_kernelProvider);
			ctx.setAttribute(PHOENIX_WEBAPP_PROVIDER_APP, m_appProvider);
			return classloader;
		} catch (Exception e) {
			throw new RuntimeException("Error when adjusting webapp classloader!", e);
		}
	}

	protected abstract void clearLoadedJars(WebappClassLoader classloader) throws Exception;

	protected ClassLoader createBootstrapClassloader() {
		try {
			List<URL> urls = new ArrayList<URL>();

			for (File entry : m_kernelProvider.getClasspathEntries()) {
				File file = entry.getCanonicalFile();

				if (file.isDirectory() || file.getPath().endsWith(".jar")) {
					urls.add(file.toURI().toURL());
				}
			}

			if (m_debug) {
				getLog().info(String.format("Bootstrap classpath: %s.", urls));
			}

			ClassLoader classloader = getClass().getClassLoader();

			if (classloader instanceof URLClassLoader) {
				Object ucp = getFieldValue(classloader, URLClassLoader.class, "ucp");
				List<URL> pathes = getFieldValue(ucp, "path");
				int len = pathes.size();

				for (int i = len - 1; i >= 0; i--) {
					URL path = pathes.get(i);

					if (shouldIgnoredByBootstrapClassloader(path)) {
						pathes.remove(i);

						if (m_debug) {
							getLog().info("Entry " + path + " ignored!");
						}
					}
				}
			}

			return new URLClassLoader(urls.toArray(new URL[0]), classloader);
		} catch (Exception e) {
			throw new RuntimeException("Unable to create bootstrap classloader!", e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getFieldValue(Object instance, Class<?> clazz, String fieldName) throws Exception {
		Field field = clazz.getDeclaredField(fieldName);

		if (!field.isAccessible()) {
			field.setAccessible(true);
		}

		return (T) field.get(instance);
	}

	@SuppressWarnings("unchecked")
	public <T> T getFieldValue(Object instance, String fieldName) throws Exception {
		return (T) getFieldValue(instance, instance.getClass(), fieldName);
	}

	public File getKernelWarRoot() {
		return m_kernelProvider.getWarRoot();
	}

	public abstract Log getLog();

	public abstract StandardContext getStandardContext();

	public abstract void finish();

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

	public File getWebXml() {
		return m_webXml;
	}

	protected <T> T loadListener(Class<T> listenerClass, ClassLoader classloader) {
		ServiceLoader<T> serviceLoader = ServiceLoader.load(listenerClass, classloader);

		for (T e : serviceLoader) {
			return e;
		}

		throw new UnsupportedOperationException("No implementation class found in phoenix-kernel war for "
		      + listenerClass);
	}

	protected void prepareWebappProviders(StandardContext ctx) {
		try {
			if (m_kernelProvider == null) {
				if (m_kernelDocBase == null) {
					throw new RuntimeException("No kernelDocBase property was set in the context!");
				}

				m_kernelProvider = new StandardWebappProvider(m_kernelDocBase);
			}

			if (m_appProvider == null) {
				String appDocBase = ctx.getDocBase();

				m_appProvider = new StandardWebappProvider(appDocBase);
			}
		} catch (IOException e) {
			throw new RuntimeException("Error when preparing webapp provider!", e);
		}
	}

	/**
	 * For development only!
	 * 
	 * @param appProvider
	 */
	public void setApplicationWebappProvider(WebappProvider appProvider) {
		m_appProvider = appProvider;
	}

	public void setDebug(String debug) {
		m_debug = "true".equals(debug);
	}

	public void setFieldValue(Object instance, Class<?> clazz, String fieldName, Object value) throws Exception {
		Field field = clazz.getDeclaredField(fieldName);

		if (!field.isAccessible()) {
			field.setAccessible(true);
		}

		field.set(instance, value);
	}

	public void setFieldValue(Object instance, String fieldName, Object value) throws Exception {
		setFieldValue(instance, instance.getClass(), fieldName, value);
	}

	/**
	 * For production only!
	 * 
	 * @param kernelDocBase
	 */
	public void setKernelDocBase(String kernelDocBase) {
		m_kernelDocBase = kernelDocBase;

		if (m_webXml == null) {
			m_webXml = new File(kernelDocBase, "WEB-INF/web.xml");
		}
	}

	/**
	 * For development only!
	 * 
	 * @param kernelProvider
	 */
	public void setKernelWebappProvider(WebappProvider kernelProvider) {
		m_kernelProvider = kernelProvider;
	}

	public void setWebXml(File webXml) {
		m_webXml = webXml;
	}

	public abstract boolean shouldIgnoredByBootstrapClassloader(URL url);

	@Override
	public void start() throws LifecycleException {
		super.start();

		m_webappClassloader = adjustWebappClassloader((WebappClassLoader) getClassLoader());
	}

	protected static class Delegate<T extends AbstractCatalinaWebappLoader, S extends LifecycleHandler<T>> implements
	      LifecycleListener {
		private T m_loader;

		private LifecycleHandler<T> m_listener;

		public Delegate(T loader, S listener) {
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
				m_loader.getLog().error(String.format("Error when dispatching " + //
				      "lifecycle event(%s) to listener(%s)!", type, m_listener.getClass().getName()), e);
			}
		}
	}

	protected static interface LifecycleHandler<T extends AbstractCatalinaWebappLoader> {
		public void afterStarted(T loader);

		public void beforeStarting(T loader);

		public void destroying(T loader);

		public void initializing(T loader);

		public void starting(T loader);

		public void stopping(T loader);
	}

	public static interface Log {
		public void debug(Object msg);

		public void debug(Object msg, Throwable e);

		public void error(Object obj);

		public void error(Object obj, Throwable e);

		public void info(Object msg);

		public void info(Object obj, Throwable e);

		public void warn(Object obj);

		public void warn(Object obj, Throwable e);
	}
}

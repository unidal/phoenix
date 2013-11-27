package com.dianping.phoenix.bootstrap;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ServiceLoader;
import java.util.jar.JarFile;

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

import com.dianping.phoenix.spi.WebappProvider;
import com.dianping.phoenix.spi.internal.MetaBasedClasspathBuilder;
import com.dianping.phoenix.spi.internal.OrderingReservedWepappProvider;
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

	private String m_appDocBase;

	public AbstractCatalinaWebappLoader() {
	}

	public AbstractCatalinaWebappLoader(ClassLoader classloader) {
		super(classloader);
	}

	protected WebappClassLoader adjustWebappClassloader(WebappClassLoader classloader) {
		try {
			// *********** Debug Infos **************
			if (m_debug) {
				logBeforeClear(classloader);
			}

			m_appProvider = new OrderingReservedWepappProvider(m_appDocBase, classloader);

			clearLoadedJars(classloader);

			initRepositories(new MetaBasedClasspathBuilder(getMetaFileFromKernelBase(m_kernelDocBase),
					getAppNameFromDocBase(m_appDocBase)).build(m_kernelProvider, m_appProvider), classloader);

			Container container = getContainer();
			ServletContext ctx = ((StandardContext) container).getServletContext();

			// pass followings to application
			ctx.setAttribute(PHOENIX_WEBAPP_LOADER, this);
			ctx.setAttribute(PHOENIX_WEBAPP_PROVIDER_KERNEL, m_kernelProvider);
			ctx.setAttribute(PHOENIX_WEBAPP_PROVIDER_APP, m_appProvider);

			// *********** Debug Infos **************
			if (m_debug) {
				logAfterClear(classloader);
			}
			return classloader;
		} catch (Exception e) {
			throw new RuntimeException("Error when adjusting webapp classloader!", e);
		}
	}

	private void logBeforeClear(WebappClassLoader classloader) {
		System.out.println("\n================== Logs before ====================");
		logWebappClassloaderStatus(classloader);
	}

	private void logAfterClear(WebappClassLoader classloader) {
		System.out.println("\n++++++++++++++++++++ Logs after++++++++++++++++++++");
		logWebappClassloaderStatus(classloader);
	}

	private void logWebappClassloaderStatus(WebappClassLoader classloader) {
		logVar("contextName", classloader);
		logVars("files", classloader);
		logVar("hasExternalRepositories", classloader);
		logJarFiles("jarFiles", classloader);
		logVars("jarNames", classloader);
		logVar("jarPath", classloader);
		logVars("jarRealFiles", classloader);
		logVar("lastJarAccessed", classloader);
		logLongs("lastModifiedDates", classloader);
		logVar("loaderDir", classloader);
		logMap("loaderPC", classloader);
		logVar("needConvert", classloader);
		logMap("notFoundResources", classloader);
		logVars("paths", classloader);
		logVar("permissionList", classloader);
		logVars("repositories", classloader);
		logVars("repositoryURLs", classloader);
		logMap("resourceEntries", classloader);
		logVar("resources", classloader);
		logVar("searchExternalFirst", classloader);
		logVar("system", classloader);
	}

	private void logJarFiles(String title, WebappClassLoader classloader) {
		try {
			System.out.println(String.format("***** %s *****", title));
			JarFile[] objs = getFieldValue(classloader, title);
			for (JarFile obj : objs) {
				System.out.println(obj.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logLongs(String title, WebappClassLoader classloader) {
		try {
			System.out.println(String.format("***** %s *****", title));
			long[] objs = getFieldValue(classloader, title);
			for (long obj : objs) {
				System.out.println(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logVar(String title, WebappClassLoader classloader) {
		try {
			Object obj = getFieldValue(classloader, title);
			System.out.println(String.format("%s = %s", title, obj));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logVars(String title, WebappClassLoader classloader) {
		try {
			System.out.println(String.format("***** %s *****", title));
			Object[] objs = getFieldValue(classloader, title);
			for (Object obj : objs) {
				System.out.println(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void logMap(String title, WebappClassLoader classloader) {
		try {
			System.out.println(String.format("***** %s *****", title));
			Map<String, Object> objs = getFieldValue(classloader, title);
			for (Entry<String, Object> entry : objs.entrySet()) {
				System.out.println(String.format("%s = %s", entry.getKey(), entry.getValue()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initRepositories(List<URL> urls, WebappClassLoader classloader) throws Exception {
		String dir = "/WEB-INF/classes/";

		setFieldValue(classloader, "repositories", new String[] { "app:" + dir, "phoenix:" + dir });
		setFieldValue(classloader, "files", new File[] { new File(m_appDocBase, dir), new File(m_kernelDocBase, dir) });

		for (URL url : urls) {
			super.addRepository(url.toExternalForm());
		}
	}

	private File getMetaFileFromKernelBase(String kernelDocBase) {
		return new File(kernelDocBase + String.format("%sMETA-INF%sKernelMeta.xml", File.separator, File.separator));
	}

	private String getAppNameFromDocBase(String appDocBase) {
		if (appDocBase.endsWith(File.separator)) {
			appDocBase = appDocBase.substring(0, appDocBase.length() - 1);
		}
		String suffix = String.format("%ssrc%smain%swebapp", File.separator, File.separator, File.separator);
		if (appDocBase.endsWith(suffix)) {
			appDocBase = appDocBase.substring(0, appDocBase.length() - suffix.length());
		}
		int lastSeparator = appDocBase.lastIndexOf(File.separator);
		appDocBase = lastSeparator < 0 ? appDocBase : appDocBase.substring(lastSeparator + 1);
		return appDocBase.endsWith(".war") ? appDocBase.substring(0, appDocBase.length() - ".war".length())
				: appDocBase;
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

	public abstract void finish();

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

	public ServletContext getServletContext() {
		Container container = super.getContainer();

		if (container instanceof Context) {
			ServletContext servletContext = ((Context) container).getServletContext();

			return servletContext;
		} else {
			throw new RuntimeException("No ServletContext was found!");
		}
	}

	public abstract StandardContext getStandardContext();

	public File getWarRoot() {
		try {
			return new File(m_appDocBase).getCanonicalFile();
		} catch (IOException e) {
			throw new RuntimeException(String.format("%s is not a valid directory", m_appDocBase), e);
		}
	}

	/**
	 * The webapp class loader should be used to load all classes for the
	 * runtime request.
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

	public Object invokeMethod(Class<?> clazz, Object instance, String methodName, Class<?>[] parameterTypes,
			Object[] parameters) throws Exception {
		System.out.println(clazz.getDeclaredMethods());
		Method method = clazz.getMethod(methodName, parameterTypes);
		if (!method.isAccessible()) {
			method.setAccessible(true);
		}
		return method.invoke(instance, parameters);
	}

	public Object invokeMethod(Object instance, String methodName, Object[] parameters) throws Exception {

		Class<?> clazz = instance.getClass();
		Class<?>[] parameterTypes = new Class<?>[parameters.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypes[i] = parameters[i].getClass();
		}

		return invokeMethod(clazz, instance, methodName, parameterTypes, parameters);
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

			m_appDocBase = ctx.getDocBase();
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

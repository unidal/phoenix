package com.dianping.phoenix.bootstrap;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

import org.apache.catalina.Container;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappClassLoader;
import org.apache.catalina.loader.WebappLoader;
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
public class Tomcat6WebappLoader extends AbstractCatalinaWebappLoader {
	private static final org.apache.juli.logging.Log LOG = LogFactory.getLog(Tomcat6WebappLoader.class);

	private Tomcat6StandardContext m_standardContext;

	public Tomcat6WebappLoader() {
	}

	public Tomcat6WebappLoader(ClassLoader classloader) {
		super(classloader);
	}

	@Override
	protected void clearLoadedJars(WebappClassLoader classloader) throws Exception {
		List<String> loaderRepositories = getFieldValue(this, WebappLoader.class, "loaderRepositories");
		URL[] repositoryURLs = getFieldValue(classloader, "repositoryURLs");

		for (int i = loaderRepositories.size() - 1; i >= 0; i--) {
			String repository = loaderRepositories.get(i);

			if (repository.endsWith(".jar")) {
				loaderRepositories.remove(i);
			}
		}

		List<URL> urls = new ArrayList<URL>();

		for (URL url : repositoryURLs) {
			if (!url.toExternalForm().endsWith(".jar")) {
				urls.add(url);
			}
		}

		setFieldValue(classloader, "repositoryURLs", urls.toArray(new URL[0]));
		setFieldValue(classloader, "jarFiles", new JarFile[0]);
		setFieldValue(classloader, "jarNames", new String[0]);
		setFieldValue(classloader, "jarRealFiles", new File[0]);
		setFieldValue(classloader, "repositories", new String[0]);
		setFieldValue(classloader, "files", new File[0]);
		setFieldValue(classloader, "paths", new String[0]);
	}

	@Override
	public Log getLog() {
		return new Log() {
			@Override
			public void debug(Object msg) {
				LOG.debug(msg);
			}

			@Override
			public void debug(Object msg, Throwable e) {
				LOG.debug(msg, e);
			}

			@Override
			public void error(Object msg) {
				LOG.error(msg);
			}

			@Override
			public void error(Object msg, Throwable e) {
				LOG.error(msg, e);
			}

			@Override
			public void info(Object msg) {
				LOG.info(msg);
			}

			@Override
			public void info(Object msg, Throwable e) {
				LOG.info(msg, e);
			}

			@Override
			public void warn(Object msg) {
				LOG.warn(msg);
			}

			@Override
			public void warn(Object msg, Throwable e) {
				LOG.warn(msg, e);
			}
		};
	}

	@Override
	public StandardContext getStandardContext() {
		return m_standardContext;
	}

	public void setContainer(Container container) {
		try {
			super.setContainer(container);
		} finally {
			StandardContext context = (StandardContext) container;

			m_standardContext = new Tomcat6StandardContext(context);

			// in case of RuntimeException threw but following code will
			// still be needed
			prepareWebappProviders(context);

			Listener listener = loadListener(Listener.class, createBootstrapClassloader());

			context.addLifecycleListener( //
			      new Delegate<Tomcat6WebappLoader, LifecycleHandler<Tomcat6WebappLoader>>(this, listener));
		}
	}

	@Override
	public boolean shouldIgnoredByBootstrapClassloader(URL url) {
		if (url.toExternalForm().contains("/jboss-common") || url.toExternalForm().contains("/jbossweb")) {
			return true; // no jboss
		}

		return false;
	}

	public static interface Listener extends LifecycleHandler<Tomcat6WebappLoader> {
	}

	@Override
	public void finish() {
		m_standardContext.finish();
	}
}

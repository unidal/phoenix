package com.dianping.phoenix.bootstrap;

import java.net.URL;

import org.apache.catalina.Container;
import org.apache.catalina.core.StandardContext;

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
	public Tomcat6WebappLoader() {
	}

	public Tomcat6WebappLoader(ClassLoader classloader) {
		super(classloader);
	}

	@Override
	protected boolean shouldIgnoredByBootstrapClassloader(URL url) {
		if (url.toExternalForm().contains("/jboss-common") || url.toExternalForm().contains("/jbossweb")) {
			return true; // no jboss
		}

		return false;
	}

	public void setContainer(Container container) {
		try {
			super.setContainer(container);
		} finally {
			// in case of RuntimeException threw but following code will
			// still be needed
			prepareWebappProviders((StandardContext) container);

			Listener listener = loadListener(Listener.class, createBootstrapClassloader());

			((StandardContext) container).addLifecycleListener( //
			      new Delegate<Tomcat6WebappLoader, LifecycleHandler<Tomcat6WebappLoader>>(this, listener));
		}
	}

	public static interface Listener extends LifecycleHandler<Tomcat6WebappLoader> {
	}
}

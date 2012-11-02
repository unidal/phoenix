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
public class Jboss4WebappLoader extends AbstractCatalinaWebappLoader {
	public Jboss4WebappLoader() {
	}

	public Jboss4WebappLoader(ClassLoader classloader) {
		super(classloader);
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
			      new Delegate<Jboss4WebappLoader, LifecycleHandler<Jboss4WebappLoader>>(this, listener));
		}
	}

	@Override
	protected boolean shouldIgnoredByBootstrapClassloader(URL url) {
		if (url.toExternalForm().contains("/catalina") || url.toExternalForm().contains("/coyate")) {
			return true; // no tomcat6
		}

		return false;
	}

	public static interface Listener extends LifecycleHandler<Jboss4WebappLoader> {
	}
}

package com.dianping.kernel.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

public class CompositeDirContextProxy implements InvocationHandler {
	private static final String APP_PREFIX = "app:";
	private static final String PHOENIX_PREFIX = "phoenix:";

	private DirContext m_appContext;
	private DirContext m_kernelContext;

	public CompositeDirContextProxy(DirContext appContext, DirContext kernelContext) {
		m_appContext = appContext;
		m_kernelContext = kernelContext;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (args.length > 0 && args[0] instanceof String) {
			String name = (String) args[0];
			if (name.startsWith(APP_PREFIX)) {
				invokeWithoutPrefix(args, APP_PREFIX, m_appContext, method);
			} else if (name.startsWith(PHOENIX_PREFIX)) {
				invokeWithoutPrefix(args, APP_PREFIX, m_appContext, method);
			}
		}

		try {
			return method.invoke(m_appContext, args);
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof NamingException) {
				try {
					return method.invoke(m_kernelContext, args);
				} catch (InvocationTargetException t) {
					throw t.getTargetException();
				}
			}

			throw e.getTargetException();
		}
	}

	private Object invokeWithoutPrefix(Object[] args, String prefix, DirContext context, Method method)
			throws Throwable {
		args[0] = ((String) args[0]).substring(prefix.length());
		try {
			return method.invoke(context, args);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
	}
}

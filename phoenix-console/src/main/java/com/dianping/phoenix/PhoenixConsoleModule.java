package com.dianping.phoenix;

import org.unidal.helper.Threads;
import org.unidal.helper.Threads.ThreadListener;
import org.unidal.initialization.AbstractModule;
import org.unidal.initialization.Module;
import org.unidal.initialization.ModuleContext;

public class PhoenixConsoleModule extends AbstractModule {
	public static final String ID = "phoenix-console";

	@Override
	protected void execute(ModuleContext ctx) throws Exception {
		ThreadListener listener = ctx.lookup(ThreadListener.class, "logger");

		Threads.addListener(listener);
	}

	@Override
	public Module[] getDependencies(ModuleContext ctx) {
		return null;
	}

	@Override
	protected void setup(ModuleContext ctx) throws Exception {
	}
}

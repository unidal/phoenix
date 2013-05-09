package com.dianping.phoenix.agent;

import org.unidal.initialization.AbstractModule;
import org.unidal.initialization.Module;
import org.unidal.initialization.ModuleContext;

import com.dianping.phoenix.agent.core.AgentStatusReporter;

public class StatusReportModule extends AbstractModule {
	public static final String ID = "status-report";

	@Override
	protected void execute(ModuleContext ctx) throws Exception {
		// start up AgentStatusReporter here
		ctx.lookup(AgentStatusReporter.class);
	}

	@Override
	public Module[] getDependencies(ModuleContext ctx) {
		return null;
	}

	@Override
	protected void setup(ModuleContext ctx) throws Exception {
	}
}

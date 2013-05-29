package com.dianping.maven.plugin.tools.generator.dynamic;

import java.io.File;
import java.io.IOException;

import com.dianping.maven.plugin.tools.velocity.VelocityEngineManager;

public class BizServerPropertiesGenerator {

	public void generate(File bizServerFile, BizServerContext ctx) throws IOException {
		VelocityEngineManager.INSTANCE.build("/bizServer.vm", ctx, bizServerFile);
	}
	
}

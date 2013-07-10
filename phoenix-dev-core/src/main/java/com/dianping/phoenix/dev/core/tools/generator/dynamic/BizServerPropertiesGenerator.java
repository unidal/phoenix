package com.dianping.phoenix.dev.core.tools.generator.dynamic;

import java.io.File;
import java.io.IOException;

import com.dianping.phoenix.dev.core.tools.velocity.VelocityEngineManager;

public class BizServerPropertiesGenerator {

	public void generate(File bizServerFile, BizServerContext ctx) throws IOException {
		VelocityEngineManager.INSTANCE.build("/bizServer.vm", ctx, bizServerFile);
	}
	
}

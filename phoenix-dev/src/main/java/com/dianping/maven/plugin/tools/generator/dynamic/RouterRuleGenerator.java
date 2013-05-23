package com.dianping.maven.plugin.tools.generator.dynamic;

import java.io.File;
import java.io.IOException;

import com.dianping.maven.plugin.phoenix.RouterRuleContext;
import com.dianping.maven.plugin.tools.velocity.VelocityEngineManager;

public class RouterRuleGenerator {

	public void generate(File routerRulesFile, RouterRuleContext ctx) throws IOException {
		VelocityEngineManager.INSTANCE.build("/router-rules.vm", ctx, routerRulesFile);
	}

}

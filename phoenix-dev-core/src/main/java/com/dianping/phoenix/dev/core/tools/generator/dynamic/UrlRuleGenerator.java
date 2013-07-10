package com.dianping.phoenix.dev.core.tools.generator.dynamic;

import java.io.File;
import java.io.IOException;

import com.dianping.phoenix.dev.core.model.workspace.entity.VirtualServer;
import com.dianping.phoenix.dev.core.tools.velocity.VelocityEngineManager;

public class UrlRuleGenerator {

	public void generate(File resourceDir, UrlRuleContext ctx) throws IOException {
		for (VirtualServer vs : ctx.getVirtualServerList()) {
			String name = vs.getName();
			File urlRuleFile = new File(resourceDir, String.format("url-rules-%s.xml", name));
			VelocityEngineManager.INSTANCE.build(String.format("/url-rules-%s.vm", name), ctx, urlRuleFile);
		}
	}

}

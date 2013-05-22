package com.dianping.maven.plugin.tools.velocity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public enum VelocityEngineManager {

	INSTANCE;
	
	private VelocityEngine ve;
	
	private VelocityEngineManager() {
		ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "class");
        ve.setProperty("class.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.setProperty("class.resource.loader.cache", true);
        ve.setProperty("class.resource.loader.modificationCheckInterval", "-1");
        ve.setProperty("input.encoding", "UTF-8");
        ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
        ve.init();
	}
	
	public void build(String tplPath, VelocityContext ctx, Writer out) {
        Template t = ve.getTemplate(tplPath);
        t.merge(ctx, out);
	}
	
	public void build(String tplPath, Object ctx, File targetFile) throws IOException {
		Template t = ve.getTemplate(tplPath);
		VelocityContext vctx = new VelocityContext();
		vctx.put("ctx", ctx);
		Writer w = new OutputStreamWriter(new FileOutputStream(targetFile), "UTF-8");
		t.merge(vctx, w);
		w.close();
	}
	
}

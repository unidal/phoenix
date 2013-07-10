package com.dianping.phoenix.dev.core.tools.velocity;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.junit.Test;

import com.dianping.phoenix.dev.core.tools.velocity.VelocityEngineManager;

public class VelocityEngineManagerTest {

	@Test
	public void willFindTemplate() {
		Writer out = new StringWriter();
		VelocityContext ctx = new VelocityContext();
		ctx.put("variable", "World");
		VelocityEngineManager.INSTANCE.build("template/test.vm", ctx, out);
		assertEquals("Hello World", out.toString());
	}

}

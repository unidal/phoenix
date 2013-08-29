package com.dianping.phoenix.dev.core.tools.generator.dynamic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.dianping.phoenix.dev.core.tools.utils.SortedProperties;

public class BizServerPropertiesGenerator {

	public void generate(File bizServerFile, BizServerContext ctx) throws IOException {
		Properties p = new SortedProperties();
		p.putAll(ctx.getCtxMap());
		OutputStream os = new FileOutputStream(bizServerFile);
		p.storeToXML(os , "", "utf-8");
		IOUtils.closeQuietly(os);
	}
	
}

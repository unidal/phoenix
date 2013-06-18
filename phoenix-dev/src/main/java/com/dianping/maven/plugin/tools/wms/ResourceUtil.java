package com.dianping.maven.plugin.tools.wms;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

public enum ResourceUtil {

	INSTANCE;

	private static Logger log = Logger.getLogger(ResourceUtil.class);

	public InputStream loadFromFileOrClasspath(File file) throws IOException {
		String fileName = file.getName();
		InputStream in;
		if (file.exists()) {
			in = new FileInputStream(file);
			log.info(String.format("read %s from %s", fileName, file.getAbsolutePath()));
		} else {
			log.info(String.format("try to read %s from classpath", fileName));
			in = this.getClass().getResourceAsStream("/" + fileName);
		}
		return in;
	}
}
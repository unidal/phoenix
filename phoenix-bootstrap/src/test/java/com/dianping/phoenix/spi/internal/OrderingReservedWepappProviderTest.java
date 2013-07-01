package com.dianping.phoenix.spi.internal;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Test;

public class OrderingReservedWepappProviderTest {
	
	@Test
	public void shouldReserveClasspathOrdering() throws Exception {
		List<File> files = new ArrayList<File>();
		files.add(new File("/shop-web/WEB-INF/classes/"));
		for (int i = 0; i < 10; i++) {
			files.add(new File(String.format("/shop-web/WEB-INF/lib/%s.jar", UUID.randomUUID())));
		}
		
		List<URL> urls = new ArrayList<URL>();
		for (File file : files) {
			urls.add(file.toURI().toURL());
		}
		
		URLClassLoader ucl = new URLClassLoader(urls.toArray(new URL[0]));
		String docBase = "/shop-web";
		OrderingReservedWepappProvider p = new OrderingReservedWepappProvider(docBase, ucl);
		
		List<File> entries = p.getClasspathEntries();
		int idx = 0;
		for (File entry : entries) {
			Assert.assertEquals(files.get(idx++), entry);
		}
		
	}
	
}

package com.dianping.phoenix.spi.internal;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.dianping.phoenix.spi.WebappProvider;

public class OrderingReservedWepappProvider implements WebappProvider {

	private List<File> classpathEntries = new ArrayList<File>();

	private File m_warRoot;

	public OrderingReservedWepappProvider(String docBase, URLClassLoader ucl) throws IOException {
		m_warRoot = new File(docBase).getCanonicalFile();
		copyClasspathEntriesFrom(ucl);
	}

	private void copyClasspathEntriesFrom(URLClassLoader ucl) {
		URL[] urls = ucl.getURLs();
		for (int i = 0; i < urls.length; i++) {
			try {
				classpathEntries.add(new File(urls[i].toURI()));
			} catch (URISyntaxException e) {
				throw new RuntimeException("error convert URL to File", e);
			}
		}
	}

	@Override
	public List<File> getClasspathEntries() {
		return classpathEntries;
	}

	@Override
	public File getWarRoot() {
		return m_warRoot;
	}

}

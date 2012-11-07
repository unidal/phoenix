package com.dianping.phoenix.spi.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dianping.phoenix.spi.WebappProvider;

public class StandardWebappProvider implements WebappProvider {
	private File m_classesDir;

	private File m_libDir;

	private File m_warRoot;

	public StandardWebappProvider(String docBase) throws IOException {
		File warRoot = new File(docBase).getCanonicalFile();

		m_classesDir = new File(warRoot, "WEB-INF/classes");
		m_libDir = new File(warRoot, "WEB-INF/lib");
		m_warRoot = warRoot;

		if (!m_warRoot.exists() || !new File(warRoot, "WEB-INF/web.xml").exists()) {
			throw new RuntimeException(String.format("Please make sure webapp at %s is a valid war!", warRoot));
		}
	}

	@Override
	public List<File> getClasspathEntries() {
		List<File> list = new ArrayList<File>();

		if (m_classesDir.isDirectory()) {
			list.add(m_classesDir);
		}

		if (m_libDir.isDirectory()) {
			File[] jarFiles = m_libDir.listFiles();

			if (jarFiles != null) {
				for (File file : jarFiles) {
					list.add(file);
				}
			}
		}

		return list;
	}

	@Override
	public File getWarRoot() {
		return m_warRoot;
	}
}
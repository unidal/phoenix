package com.dianping.phoenix.bootstrap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StandardWebappProvider implements WebappProvider {
	private File m_classesDir;

	private File m_libDir;

	private File m_warRoot;

	public StandardWebappProvider(String docBase) throws IOException {
		File warRoot = new File(docBase).getCanonicalFile();

		m_classesDir = new File(warRoot, "WEB-INF/classes");
		m_libDir = new File(warRoot, "WEB-INF/lib");
		m_warRoot = warRoot;

		if (!m_warRoot.exists() || !m_libDir.exists()) {
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
			String[] names = m_libDir.list();

			if (names != null) {
				for (String name : names) {
					File jarFile = new File(m_libDir, name);

					list.add(jarFile);
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
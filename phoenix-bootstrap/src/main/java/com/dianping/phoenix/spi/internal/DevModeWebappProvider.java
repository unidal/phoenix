package com.dianping.phoenix.spi.internal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.dianping.phoenix.spi.WebappProvider;

public class DevModeWebappProvider implements WebappProvider {
	private File m_classesDir;

	private File m_libDir;

	private File m_warRoot;

	public DevModeWebappProvider(String baseDir, String warName) throws IOException {
		m_classesDir = new File(baseDir, "target/classes").getCanonicalFile();
		m_libDir = new File(baseDir, "target/" + warName + "/WEB-INF/lib").getCanonicalFile();
		m_warRoot = new File(baseDir, "src/main/webapp").getCanonicalFile();

		if (!m_warRoot.exists()) {
			throw new RuntimeException(String.format("Please make sure project at %s is a valid war "
			      + "with src/main/webapp folder!", baseDir));
		}

		if (!m_libDir.exists()) {
			throw new RuntimeException(String.format("You need to run 'mvn package' for project at %s once "
			      + "before starting the server!", baseDir));
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
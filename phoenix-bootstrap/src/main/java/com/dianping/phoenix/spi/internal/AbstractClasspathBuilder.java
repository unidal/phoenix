package com.dianping.phoenix.spi.internal;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dianping.phoenix.spi.ClasspathBuilder;
import com.dianping.phoenix.spi.WebappProvider;

public abstract class AbstractClasspathBuilder implements ClasspathBuilder {
	@Override
	public List<URL> build(WebappProvider kernelProvider, WebappProvider appProvider) {
		List<URL> urls = new ArrayList<URL>(256);
		Map<String, Entry> kernelEntries = new LinkedHashMap<String, Entry>();

		for (File e : kernelProvider.getClasspathEntries()) {
			Entry entry = new Entry(e);

			if (entry.isJar()) {
				kernelEntries.put(entry.getArtifactId(), entry);
			} else {
				kernelEntries.put(entry.getFile().getPath(), entry);
			}
		}

		try {
			for (File e : appProvider.getClasspathEntries()) {
				Entry appEntry = new Entry(e);

				if (appEntry.isJar()) {
					Entry kernelEntry = kernelEntries.remove(appEntry.getArtifactId());

					if (kernelEntry == null) {
						urls.add(appEntry.getFile().toURI().toURL());
					} else {
						Entry entry = pickup(kernelEntry, appEntry);
						if (entry != null) {
							urls.add(entry.getFile().toURI().toURL());
						}
					}
				} else {
					urls.add(appEntry.getFile().toURI().toURL());
				}
			}

			for (Entry kernelEntry : kernelEntries.values()) {
				urls.add(kernelEntry.getFile().toURI().toURL());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return urls;
	}

	protected abstract Entry pickup(Entry kernelEntry, Entry appEntry);

	protected static class Entry {
		private File m_file;

		private String m_artifactId;

		private String m_version;

		private boolean m_jar;

		public Entry(File file) {
			m_file = file;

			String path = file.getPath();

			if (path.endsWith(".jar")) {
				int len = path.length();
				int pos = path.lastIndexOf(File.separatorChar);

				VersionParser parser = new VersionParser();
				String[] result = parser.parse(path.substring(pos + 1, len - 4));

				m_jar = true;
				m_artifactId = result[0];
				m_version = result[1];
			}
		}

		public String getArtifactId() {
			return m_artifactId;
		}

		public File getFile() {
			return m_file;
		}

		public String getVersion() {
			return m_version;
		}

		public boolean isJar() {
			return m_jar;
		}
	}
}

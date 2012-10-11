package com.dianping.phoenix.bootstrap;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This builder will pick up a higher compatible jar from kernel webapp if have,
 * and replace it in the application webapp.
 */
public class DefaultClasspathBuilder implements ClasspathBuilder {
	@Override
	public List<URL> build(WebappProvider kernelProvider, WebappProvider appProvider) {
		List<URL> urls = new ArrayList<URL>(256);
		Map<String, Entry> kernelEntries = new LinkedHashMap<String, Entry>();
		VersionComparator comparator = new VersionComparator();

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
				Entry entry = new Entry(e);

				if (entry.isJar()) {
					Entry kernelEntry = kernelEntries.remove(entry.getArtifactId());

					if (kernelEntry != null && comparator.compare(kernelEntry.getVersion(), entry.getVersion()) > 0) {
						urls.add(kernelEntry.getFile().toURI().toURL());
					} else {
						urls.add(entry.getFile().toURI().toURL());
					}
				} else {
					urls.add(entry.getFile().toURI().toURL());
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
				int pos = path.lastIndexOf('/');

				m_jar = true;
				parse(path.substring(pos + 1, len - 4));
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

		private void parse(String str) {
			int fromIndex = str.length() - 1;

			do {
				int pos = str.lastIndexOf('-', fromIndex);

				if (pos > 0) {
					char ch = str.charAt(pos + 1);

					if (Character.isDigit(ch)) {
						String right = str.substring(pos + 1);

						if (right.indexOf('.') > 0) { // for "a-1.1-1.jar"
							m_version = right;
							m_artifactId = str.substring(0, pos);
							break;
						}
					}

					fromIndex = pos - 1;
				}
			} while (fromIndex > 0);
		}
	}

	protected static class VersionComparator implements Comparator<String> {
		@Override
		public int compare(String v1, String v2) {
			if (v1 == null || v2 == null) {
				return 0; // not comparable
			}

			StringTokenizer t1 = new StringTokenizer(v1, ".-", true);
			StringTokenizer t2 = new StringTokenizer(v2, ".-", true);
			boolean patch1 = false;
			boolean patch2 = false;

			while (true) {
				String p1 = t1.hasMoreTokens() ? t1.nextToken() : null;
				String p2 = t2.hasMoreTokens() ? t2.nextToken() : null;

				if (p1 != null && p2 != null) {
					if (patch1 || patch2) {
						if (patch1 == patch2) {
							// SNAPSHOT is always lowest version number
							if (p1.equals("SNAPSHOT") && !p2.equals("SNAPSHOT")) {
								return -1;
							}

							if (p2.equals("SNAPSHOT") && !p1.equals("SNAPSHOT")) {
								return 1;
							}

							return p1.compareToIgnoreCase(p2);
						} else {
							return patch1 ? -1 : 1;
						}
					} else {
						if (!p1.equals(p2)) {
							try {
								int i1 = Integer.parseInt(p1);
								int i2 = Integer.parseInt(p2);

								return i1 - i2;
							} catch (NumberFormatException e) {
								return p1.compareTo(p2);
							}
						} else {
							String n1 = t1.hasMoreTokens() ? t1.nextToken() : null;
							String n2 = t2.hasMoreTokens() ? t2.nextToken() : null;

							if (n1 != null && n1.equals("-")) {
								patch1 = true;
							}

							if (n2 != null && n2.equals("-")) {
								patch2 = true;
							}
						}
					}
				} else if (p1 == null && p2 == null) {
					return 0;
				} else if (p1 == null) {
					return patch2 ? 1 : -1;
				} else if (p2 == null) {
					return patch1 ? -1 : 1;
				}
			}
		}
	}
}

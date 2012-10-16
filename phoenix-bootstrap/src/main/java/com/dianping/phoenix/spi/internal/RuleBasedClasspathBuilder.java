package com.dianping.phoenix.spi.internal;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.xml.sax.SAXException;

import com.dianping.phoenix.bootstrap.rule.entity.Config;
import com.dianping.phoenix.bootstrap.rule.entity.Rule;
import com.dianping.phoenix.bootstrap.rule.transform.DefaultMerger;
import com.dianping.phoenix.bootstrap.rule.transform.DefaultSaxParser;
import com.dianping.phoenix.spi.WebappProvider;

public class RuleBasedClasspathBuilder extends AbstractClasspathBuilder {
	private VersionComparator m_comparator = new VersionComparator();

	private Config m_config;

	@Override
	public List<URL> build(WebappProvider kernelProvider, WebappProvider appProvider) {
		try {
			loadConfig(kernelProvider, appProvider);
		} catch (Exception e) {
			throw new RuntimeException(
			      String.format("Error when loading config for project: %s!", getWarName(appProvider)), e);
		}

		return super.build(kernelProvider, appProvider);
	}

	private Rule findRule(Entry entry) {
		String artifactId = entry.getArtifactId();
		Rule rule = m_config.findRule(artifactId);

		if (rule == null) {
			rule = m_config.findRule("*");
		}

		if (rule != null) {
			VersionMatcher matcher = rule.getMatcher();

			if (matcher == null) {
				matcher = new VersionMatcher(rule.getVersion());

				rule.setMatcher(matcher);
			}

			if (matcher.matches(entry.getVersion())) {
				return rule;
			}
		}

		return null;
	}

	private String getWarName(WebappProvider provider) {
		File warRoot = provider.getWarRoot();
		String path = warRoot.getPath();

		if (path.endsWith("src/main/webapp")) {
			try {
				return new File(warRoot, "../../..").getCanonicalFile().getName();
			} catch (IOException e) {
				throw new RuntimeException("Unable to resolve file: " + new File(warRoot, "../../.."), e);
			}
		} else {
			return warRoot.getName();
		}
	}

	private void loadConfig(WebappProvider kernelProvider, WebappProvider appProvider) throws SAXException, IOException {
		File warRoot = kernelProvider.getWarRoot();
		String appName = getWarName(appProvider);
		File defaultFile = new File(warRoot, "META-INF/rule/_DEFAULT_.xml");
		File file = new File(warRoot, "META-INF/rule/" + appName + ".xml");
		DefaultMerger merger = new DefaultMerger(new Config());

		if (defaultFile.isFile()) {
			Config config = DefaultSaxParser.parse(new FileReader(defaultFile));

			config.accept(merger);
		}

		if (file.isFile()) {
			Config config = DefaultSaxParser.parse(new FileReader(file));

			config.accept(merger);
		}

		m_config = merger.getConfig();
	}

	@Override
	protected Entry pickup(Entry kernelEntry, Entry appEntry) {
		Rule rule = findRule(appEntry);
		String use = rule == null ? "higher" : rule.getUse();

		if ("kernel".equals(use)) {
			return kernelEntry;
		} else if ("app".equals(use)) {
			return appEntry;
		} else if ("higher".equals(use)) {
			// pick up a higher version jar
			if (m_comparator.compare(kernelEntry.getVersion(), appEntry.getVersion()) > 0) {
				return kernelEntry;
			} else {
				return appEntry;
			}
		} else if ("lower".equals(use)) {
			// pick up a lower version jar
			if (m_comparator.compare(kernelEntry.getVersion(), appEntry.getVersion()) < 0) {
				return kernelEntry;
			} else {
				return appEntry;
			}
		} else {
			throw new IllegalStateException(String.format(
			      "Only values(kernel,app,higher,lower) are supported by rule/@use, but was %s!", use));
		}
	}
}

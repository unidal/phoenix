package com.dianping.phoenix.spi.internal;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.dianping.phoenix.bootstrap.meta.entity.Meta;
import com.dianping.phoenix.bootstrap.meta.entity.Rule;
import com.dianping.phoenix.bootstrap.meta.transform.DefaultSaxParser;

public class MetaBasedClasspathBuilder extends AbstractClasspathBuilder {
	private VersionComparator m_comparator = new VersionComparator();
	private VersionRangeChecker m_rangeChecker = new VersionRangeChecker();
	private List<Rule> m_rules = new ArrayList<Rule>();

	public MetaBasedClasspathBuilder(File metafile, String app) {
		if (metafile.exists()) {
			try {
				Meta meta = DefaultSaxParser.parse(new FileInputStream(metafile));
				if (meta != null && app != null) {
					for (Rule rule : meta.getRules()) {
						if (rule.getArtifactIdPattern() != null && rule.getConflict() != null
								&& rule.getSolution() != null
								&& (rule.getDomains().size() == 0 || rule.getDomains().contains(app))) {
							m_rules.add(rule);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected Entry pickup(Entry kernelEntry, Entry appEntry) {
		if (m_rules.size() > 0) {
			for (Rule rule : m_rules) {
				// artifactId, kernel version, app version matches
				System.out.println(String.format("Rule: %s", rule.getArtifactIdPattern()));
				if (Pattern.matches(rule.getArtifactIdPattern(), kernelEntry.getArtifactId())
						&& m_rangeChecker.matches(rule.getConflict().getKernelVersionRange(), kernelEntry.getVersion())
						&& m_rangeChecker.matches(rule.getConflict().getAppVersionRange(), appEntry.getVersion())) {
					return "app".equals(rule.getSolution()) ? appEntry : kernelEntry;
				}
			}
		}
		return defaultPickup(kernelEntry, appEntry);
	}

	protected Entry defaultPickup(Entry kernelEntry, Entry appEntry) {
		if (m_comparator.compare(kernelEntry.getVersion(), appEntry.getVersion()) > 0) {
			return kernelEntry;
		} else {
			return appEntry;
		}
	}

}

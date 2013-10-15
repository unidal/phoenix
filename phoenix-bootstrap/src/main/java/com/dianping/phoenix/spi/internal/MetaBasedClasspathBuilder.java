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
						if (rule.getJarPattern() != null && isDomainMatch(rule, app)) {
							m_rules.add(rule);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isDomainMatch(Rule rule, String domain) {
		if (rule.getIncludeDomains().size() == 0
				&& (rule.getExcludeDomains().size() == 0 || !rule.getExcludeDomains().contains(domain))) {
			return true;
		} else if (rule.getIncludeDomains().size() != 0 && rule.getIncludeDomains().contains(domain)) {
			return true;
		}
		return false;
	}

	@Override
	protected Entry pickup(Entry kernelEntry, Entry appEntry) {
		for (Rule rule : m_rules) {
			// artifactId, kernel version, app version matches
			if (Pattern.matches(rule.getJarPattern(), kernelEntry.getArtifactId())
					&& m_rangeChecker.matches(rule.getVersionInKernel(), kernelEntry.getVersion())
					&& m_rangeChecker.matches(rule.getVersionInApp(), appEntry.getVersion())) {
				PickupPolicy policy = PickupPolicy.getByName(rule.getPolicy());
				if (policy != null) {
					switch (policy) {
						case USE_APP :
							return appEntry;
						case USE_KERNEL :
							return kernelEntry;
						case USE_NEITHER :
							return null;
						default :
							break;
					}
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

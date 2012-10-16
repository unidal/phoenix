package com.dianping.phoenix.spi.internal;

/**
 * This builder will pick up a higher compatible jar from kernel webapp if have,
 * and replace it into the application webapp.
 */
public class DefaultClasspathBuilder extends AbstractClasspathBuilder {
	private VersionComparator m_comparator = new VersionComparator();

	@Override
	protected Entry pickup(Entry kernelEntry, Entry appEntry) {
		if (m_comparator.compare(kernelEntry.getVersion(), appEntry.getVersion()) > 0) {
			return kernelEntry;
		} else {
			return appEntry;
		}
	}
}

package com.dianping.phoenix.spi.internal;

import junit.framework.Assert;

import org.junit.Test;

import com.dianping.phoenix.spi.internal.VersionComparator;

public class VersionComparatorTest {
	private void checkVersion(String v2, String v1) {
		VersionComparator comparator = new VersionComparator();

		Assert.assertTrue(String.format("Not backcompatible to upgrade version from %s to %s!", v2, v1),
		      comparator.compare(v1, v2) > 0);
	}

	@Test
	public void testVersionCompatible() {
		checkVersion("3.2.7-Final", "3.2.7");
		checkVersion("3.2.7", "3.2.7.Final");
		checkVersion("3.2.6", "3.2.7.Final");
		checkVersion("1.6-rc1", "1.6-rc2");
		checkVersion("1.5-rc2", "1.6-rc1");
		checkVersion("1.6-rc1", "1.6");
		checkVersion("1.6-beta", "1.6");
		checkVersion("1.6-alpha", "1.6");
		checkVersion("1.5", "1.6-SNAPSHOT");
		checkVersion("1.5-SNAPSHOT", "1.6-SNAPSHOT");
		checkVersion("1.6-SNAPSHOT", "1.6-rc1");
		checkVersion("1.6-SNAPSHOT", "1.6.1");
		checkVersion("1.6-SNAPSHOT", "1.6");
		checkVersion("1.6-1", "1.6");
		checkVersion("1.5.3-1", "1.6");
		checkVersion("1.5.3.1", "1.6");
		checkVersion("1.5.3", "1.6");
		checkVersion("1.5", "1.5.3");
		checkVersion("1.5.2", "1.5.3");
		checkVersion("1.6.0", "1.7.1");
	}
}

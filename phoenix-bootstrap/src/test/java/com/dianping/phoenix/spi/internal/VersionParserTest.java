package com.dianping.phoenix.spi.internal;

import junit.framework.Assert;

import org.junit.Test;

public class VersionParserTest {
	private void checkVersion(String jarName, String expectedArtifactId, String expectedVersion) {
		VersionParser parser = new VersionParser();
		String[] result = parser.parse(jarName);

		Assert.assertEquals("Expected artifactId does not match!", expectedArtifactId, result[0]);
		Assert.assertEquals("Expected version does not match!", expectedVersion, result[1]);
	}

	@Test
	public void testParse() {
		checkVersion("dpsf-net-1.6.1", "dpsf-net", "1.6.1");
		checkVersion("netty-3.2.7-Final", "netty", "3.2.7-Final");
		checkVersion("myjar-1.6-rc1", "myjar", "1.6-rc1");
		checkVersion("phoenix-kernel-1.6-beta", "phoenix-kernel", "1.6-beta");
		checkVersion("phoenix-kernel-1.5", "phoenix-kernel", "1.5");
		checkVersion("phoenix-kernel-1.5-SNAPSHOT", "phoenix-kernel", "1.5-SNAPSHOT");
		checkVersion("phoenix-kernel-1.6-1", "phoenix-kernel", "1.6-1");
		checkVersion("phoenix-kernel-1.5.3-1", "phoenix-kernel", "1.5.3-1");
		checkVersion("json-20090211", "json", "20090211");
		checkVersion("sqljdbc-4", "sqljdbc", "4");
	}
}

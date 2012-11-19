package com.dianping.phoenix.service;

import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

public class VersionManagerTest extends ComponentTestCase {
	@Test
	public void test() throws Exception {
		VersionManager manager = lookup(VersionManager.class);

		manager.createVersion("mock-1.0", "mock description", "this is release notes", "mock");
		manager.removeVersion("mock-1.0");
	}
}

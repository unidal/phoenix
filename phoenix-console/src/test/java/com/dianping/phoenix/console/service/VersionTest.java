package com.dianping.phoenix.console.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.unidal.lookup.ComponentTestCase;

@RunWith(JUnit4.class)
public class VersionTest extends ComponentTestCase {
	@Test
	public void testCreate() throws Exception {
		VersionService service = lookup(VersionService.class);

		service.createVersion("0.1-SNAPSHOT", "[2012-11-15 16:19:00] by Frankie");
	}

	@Test
	public void testRemove() throws Exception {
		VersionService service = lookup(VersionService.class);

		service.removeVersion("0.1-SNAPSHOT");
	}
}

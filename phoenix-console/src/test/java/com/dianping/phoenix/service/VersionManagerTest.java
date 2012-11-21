package com.dianping.phoenix.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.console.dal.deploy.Version;

public class VersionManagerTest extends ComponentTestCase {
	@Test
	public void test() throws Exception {
		VersionManager manager = lookup(VersionManager.class);
		GitService git = lookup(GitService.class);

		git.setup();

		String tag = "mock-1.0" + System.currentTimeMillis();
		Version version = manager.createVersion(tag, "mock description", "this is release notes", "mock");
		List<Version> versions = manager.getActiveVersions();
		Assert.assertTrue(versions.size() > 0);
		
		manager.removeVersion(version.getId());
	}
}

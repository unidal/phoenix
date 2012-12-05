package com.dianping.phoenix.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.console.dal.deploy.Version;
import com.dianping.phoenix.console.page.version.DefaultVersionManager.VersionLog;
import com.dianping.phoenix.console.page.version.VersionContext;
import com.dianping.phoenix.console.page.version.VersionManager;

public class VersionManagerTest extends ComponentTestCase {

	VersionManager manager;

	@Before
	public void init() throws Exception {
		manager = lookup(VersionManager.class);
	}

	@Test
	public void testSubmitVersion() throws Exception {

		GitService git = lookup(GitService.class);

		String tag = "mock-1.0" + System.currentTimeMillis();
		VersionContext context = new VersionContext(0, tag, "mock description",
				"test", "mock");

		git.setup(context);

		Version version = manager
				.store(tag, "mock description", "test", "mock");

		manager.submitVersion(context);

		version = manager.getActiveVersion();
		Assert.assertTrue(version != null);

		manager.updateVersionSuccessed(version.getId());

		List<Version> versions = manager.getFinishedVersions();

		Assert.assertTrue(versions.size() > 0);

		for (final Version v : versions) {
			manager.removeVersion(v.getId());
		}

	}

	@Test
	public void testManageVersion() throws Exception {
		String tag = "mock-1.0" + System.currentTimeMillis();
		Version version = manager.createVersion(tag, "mock description",
				"test", "mock");

		Thread.sleep(5 * 1000);

		VersionLog log = manager.getStatus(version.getVersion(), 0);

		Assert.assertNotNull(log);

	}
}

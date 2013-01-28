package com.dianping.phoenix.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.unidal.lookup.ComponentTestCase;

import com.dianping.phoenix.console.dal.deploy.Deliverable;
import com.dianping.phoenix.version.VersionManager;
import com.dianping.phoenix.version.VersionManager.VersionLog;

public class VersionManagerTest extends ComponentTestCase {
	private VersionManager m_manager;

	@Before
	public void init() throws Exception {
		m_manager = lookup(VersionManager.class);
	}

	@Test
	public void testSubmitVersion() throws Exception {
//		GitService git = lookup(GitService.class);
//		String tag = "mock-1.0-" + System.currentTimeMillis();
//		VersionContext context = new VersionContext("phoenix-kernel", 0, tag, "mock description", "test", "mock");
//
//		git.setup(context);
//
//		Deliverable version = m_manager.store(tag, "mock description", "test", "mock");
//
//		m_manager.submitVersion(context);
//
//		String activeVersion = m_manager.getActiveVersion("phoenix-kernel");
//
//		Assert.assertTrue(activeVersion != null);
//
//		m_manager.updateVersionStatus(version.getId(), 2);
//
//		List<Deliverable> versions = m_manager.getFinishedVersions("kernel");
//
//		Assert.assertTrue(versions.size() > 0);
//
//		for (final Deliverable v : versions) {
//			m_manager.removeVersion(v.getId());
//		}
	}

	@Test
	public void testManageVersion() throws Exception {
		String tag = "mock-1.0" + System.currentTimeMillis();
		Deliverable version = m_manager.addVersion("phoenix-kernel", tag, "mock description", "test", "mock");

		Thread.sleep(5 * 1000);

		VersionLog log = m_manager.getStatus(version.getWarVersion(), 0);

		Assert.assertNotNull(log);
	}
}
